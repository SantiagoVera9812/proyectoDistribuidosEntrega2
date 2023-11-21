package org.example;

import org.zeromq.ZMQ;

public class ServidorManejoDeFallos {
    public static void main(String[] args) {
        // Configuración de ZeroMQ
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket socket = context.socket(ZMQ.REP);
             ZMQ.Socket reqSocket = context.socket(ZMQ.REQ)) {

            // El servidor escucha en el puerto 5555
            socket.bind("tcp://*:5559");

            while (true) {
                // Espera un mensaje de registro de un nuevo monitor
                String mensajeRegistro = socket.recvStr();
                System.out.println("Nuevo monitor registrado: " + mensajeRegistro);

                // Realiza alguna lógica (por ejemplo, enviar confirmación al monitor)
                String respuesta = "Monitor registrado exitosamente";
                socket.send(respuesta);

                // Envia una solicitud al monitor (ejemplo)
                reqSocket.send("Solicitud al monitor");

                // Espera la respuesta del monitor
                String respuestaMonitor = reqSocket.recvStr();
                System.out.println("Respuesta del monitor: " + respuestaMonitor);

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
