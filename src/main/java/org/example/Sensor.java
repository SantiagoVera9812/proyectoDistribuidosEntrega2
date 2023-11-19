package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.zeromq.ZMQ;

public class Sensor {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso incorrecto. Debes proporcionar el tipo de sensor, la frecuencia y el archivo de configuración.");
            System.exit(1);
        }

        String tipoSensor = args[0];
        int frecuencia = Integer.parseInt(args[1]);
        String archivoConfiguracion = args[2];

        double probabilidadCorrecto = 0.0;
        double probabilidadFueraDeRango = 0.0;
        double probabilidadInvalido = 0.0;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoConfiguracion))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(" ");
                probabilidadCorrecto = Double.parseDouble(partes[0]);
                probabilidadFueraDeRango = Double.parseDouble(partes[1]);
                probabilidadInvalido = Double.parseDouble(partes[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket publisher = context.socket(ZMQ.PUB)) {
            publisher.connect("tcp://localhost:5555"); // Se conecta al broker

            Random random = new Random();

            while (!Thread.currentThread().isInterrupted()) {
                double aleatorio = random.nextDouble();
                String mensaje = generarMedicion(aleatorio, probabilidadCorrecto, probabilidadFueraDeRango, probabilidadInvalido);
                publisher.send(tipoSensor + " " + mensaje);
                Thread.sleep(frecuencia * 1000); // Convierte la frecuencia a milisegundos
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generarMedicion(double aleatorio, double probabilidadCorrecto, double probabilidadFueraDeRango, double probabilidadInvalido) {
        if (aleatorio < probabilidadCorrecto) {
            // Genera un valor dentro del rango correcto
            return generarValorDentroDelRango();
        } else if (aleatorio < probabilidadCorrecto + probabilidadFueraDeRango) {
            // Genera un valor fuera del rango
            return generarValorFueraDelRango();
        } else {
            // Genera un valor inválido (por ejemplo, negativo)
            return generarValorInvalido();
        }
    }

    public static String generarValorDentroDelRango() {
        
        return String.valueOf(new Random().nextInt(101)); 
    }

    public static String generarValorFueraDelRango() {
        
        return String.valueOf(new Random().nextInt(200) + 101); // Valores fuera del rango
    }

    public static String generarValorInvalido() {
        
        return String.valueOf(-1);
    }
}
