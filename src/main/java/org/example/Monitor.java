package org.example;
import java.rmi.Naming;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            System.out.println("Uso incorrecto. Debes proporcionar el tipo de sensor que vas a monitorear");
            System.exit(1);
        }
        String tipoSensor = args[0];
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
             ZMQ.Socket publisher = context.socket(ZMQ.PUB);
             ZMQ.Socket registroSocket = context.socket(ZMQ.REQ);
             ZMQ.Socket reqSocket = context.socket(ZMQ.REQ)) {
            subscriber.connect("tcp://localhost:5553"); // Se conecta al broker
            subscriber.subscribe(tipoSensor.getBytes()); 
            // Suscribe a mensajes con la etiqueta "topicA"

            // Conexión al servidor de manejo de fallos
            registroSocket.connect("tcp://localhost:5559");

            // Envía un mensaje de registro al servidor de manejo de fallos
            registroSocket.send("Registro de Monitor " + tipoSensor);
            while (true) {
                try{
                String mensaje = new String(subscriber.recv());
                System.out.println("Mensaje recibido: " + mensaje);
                String [] splittedMessage = mensaje.split(" ");
                publisher.connect("tcp://localhost:5557"); // Se conecta al sistema calidad
                double medicion = Double.parseDouble(splittedMessage[1]);
                switch(tipoSensor){
                    case "temperatura":
                        if(medicion<68 || medicion>89){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    case "PH":
                        if(medicion<6.0 || medicion>8.0){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    case "oxigeno":
                        if(medicion<2 || medicion > 11){
                            publisher.send("Alerta: Medicióna normal en el sensor: " + tipoSensor + " - " + medicion);
                        }
                        break;
                    default:
                        System.out.println("Tipo de sensor no valido");
                        break;
                }
                callRmiGuardarMensaje("archivo.txt", mensaje);
                /* reqSocket.connect("tcp://localhost:5559");
                String solicitud = reqSocket.recvStr();
                System.out.println("Solicitud del servidor: " + solicitud);
                
                // Realiza alguna lógica en respuesta a la solicitud (ejemplo)
                String respuestaSolicitud = "Respuesta a la solicitud";
                reqSocket.send(respuestaSolicitud);
                */
                Thread.sleep(1000);
                
            }catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }
    }
    
     private static void callRmiGuardarMensaje(String nombreArchivo, String mensaje) {
        try {
            String serverUrl = "rmi://localhost:1237/GuardarMensajeService";
            GuardarMensajeService guardarMensajeService = (GuardarMensajeService) Naming.lookup(serverUrl);

            // Call the RMI method to save the message on the server
            guardarMensajeService.guardarMensajeEnArchivo(nombreArchivo, mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
