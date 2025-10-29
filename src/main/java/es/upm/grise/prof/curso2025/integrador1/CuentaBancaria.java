// java
package es.upm.grise.prof.curso2025.integrador1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CuentaBancaria {

    public String numeroCuenta;
    public double saldoInicial;
    public boolean admiteDescubierto = false;
    public List<Operacion> operaciones;

    public CuentaBancaria(String numeroCuenta, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.saldoInicial = saldoInicial;
        this.operaciones = new ArrayList<>();
    }

    public void addOperacion(Operacion operacion) {
        if (operacion == null) {
            return; // evitar NPE y no añadir nulls
        }
        if (operaciones == null) {
            operaciones = new ArrayList<>();
        }
        if (!operaciones.contains(operacion)) { // evita duplicados por identidad/equals
            operaciones.add(operacion);
        }
    }

    public double getSaldoActual() {
        double total = saldoInicial;
        if (operaciones != null) {
            for (Operacion op : operaciones) {
                if (op != null) {
                    total += op.getImporte();
                }
            }
        }
        if (!admiteDescubierto && total < 0.0) {
            total = 0.0;
        }
        BigDecimal bd = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
