// java
package es.upm.grise.prof.curso2025.integrador1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaBancariaTest {

    @Mock
    private Operacion operacion1;

    @Mock
    private Operacion operacion2;

    private CuentaBancaria cuenta;

    @BeforeEach
    void setUp() {
        cuenta = new CuentaBancaria("ES1234567890", 1000.0);
    }

    @Test
    void testConstructorInicializaCuentaCorrectamente() {
        assertEquals("ES1234567890", cuenta.numeroCuenta);
        assertEquals(1000.0, cuenta.saldoInicial);
        assertFalse(cuenta.admiteDescubierto);
        assertNotNull(cuenta.operaciones);
        assertTrue(cuenta.operaciones.isEmpty());
    }

    @Test
    void testAddOperacionNula() {
        cuenta.addOperacion(null);
        assertTrue(cuenta.operaciones.isEmpty());
    }

    @Test
    void testAddOperacionValida() {
        // no stubbing innecesario
        cuenta.addOperacion(operacion1);

        assertEquals(1, cuenta.operaciones.size());
        assertTrue(cuenta.operaciones.contains(operacion1));
    }

    @Test
    void testAddOperacionDuplicada() {
        // no stubbing innecesario
        cuenta.addOperacion(operacion1);
        cuenta.addOperacion(operacion1);

        assertEquals(1, cuenta.operaciones.size());
    }

    @Test
    void testGetSaldoActualSinOperaciones() {
        double saldo = cuenta.getSaldoActual();
        assertEquals(1000.0, saldo);
    }

    @Test
    void testGetSaldoActualConOperacionPositiva() {
        when(operacion1.getImporte()).thenReturn(500.0);

        cuenta.addOperacion(operacion1);

        assertEquals(1500.0, cuenta.getSaldoActual());
    }

    @Test
    void testGetSaldoActualConOperacionNegativa() {
        when(operacion1.getImporte()).thenReturn(-300.0);

        cuenta.addOperacion(operacion1);

        assertEquals(700.0, cuenta.getSaldoActual());
    }

    @Test
    void testGetSaldoActualConVariasOperaciones() {
        when(operacion1.getImporte()).thenReturn(200.0);
        when(operacion2.getImporte()).thenReturn(-150.0);

        cuenta.addOperacion(operacion1);
        cuenta.addOperacion(operacion2);

        assertEquals(1050.0, cuenta.getSaldoActual());
    }

    @Test
    void testGetSaldoActualRedondeoADosDecimales() {
        when(operacion1.getImporte()).thenReturn(0.123);
        when(operacion2.getImporte()).thenReturn(0.456);

        cuenta.addOperacion(operacion1);
        cuenta.addOperacion(operacion2);

        double saldo = cuenta.getSaldoActual();
        assertEquals(1000.58, saldo);
    }

    @Test
    void testGetSaldoActualSinDescubiertoDevuelveCero() {
        when(operacion1.getImporte()).thenReturn(-1200.0);

        cuenta.addOperacion(operacion1);

        assertEquals(0.0, cuenta.getSaldoActual());
    }

    @Test
    void testGetSaldoActualConDescubiertoPermiteSaldoNegativo() {
        cuenta.admiteDescubierto = true;
        when(operacion1.getImporte()).thenReturn(-1200.0);

        cuenta.addOperacion(operacion1);

        assertEquals(-200.0, cuenta.getSaldoActual());
    }

    @Test
    void testGetSaldoActualLimiteDescubierto() {
        when(operacion1.getImporte()).thenReturn(-1000.0);

        cuenta.addOperacion(operacion1);

        assertEquals(0.0, cuenta.getSaldoActual());
    }

    @Test
    void testRedondeoConTresDecimales() {
        cuenta = new CuentaBancaria("ES0000000000", 10.123);

        double saldo = cuenta.getSaldoActual();

        assertEquals(10.12, saldo);
    }

    @Test
    void testRedondeoConCuatroDecimales() {
        cuenta = new CuentaBancaria("ES0000000000", 10.1236);

        double saldo = cuenta.getSaldoActual();

        assertEquals(10.12, saldo);
    }

    @Test
    void testRedondeoHaciaArriba() {
        cuenta = new CuentaBancaria("ES0000000000", 10.126);

        double saldo = cuenta.getSaldoActual();

        assertEquals(10.13, saldo);
    }


    @Test
    void testAddOperacionCuandoOperacionesNull() {
        CuentaBancaria cuenta = new CuentaBancaria("ES1111111111", 100.0);
        cuenta.operaciones = null; // forzar la rama que inicializa la lista dentro de addOperacion

        Operacion op = mock(Operacion.class);

        cuenta.addOperacion(op);

        assertNotNull(cuenta.operaciones);
        assertEquals(1, cuenta.operaciones.size());
        assertTrue(cuenta.operaciones.contains(op));
    }

    // java
    @Test
    void testAddOperacionDuplicadaPorEquals() {
        CuentaBancaria cuenta = new CuentaBancaria("ES2222222222", 0.0);

        TestOperacion op1 = new TestOperacion("id-igual", 10.0);
        TestOperacion op2 = new TestOperacion("id-igual", 5.0); // same id -> equals true

        cuenta.addOperacion(op1);
        cuenta.addOperacion(op2); // debería considerarse duplicada por equals

        assertEquals(1, cuenta.operaciones.size());
        assertTrue(cuenta.operaciones.contains(op1));
    }


    @Test
    void testAddOperacionDuplicadaPorIdentidad() {
        CuentaBancaria cuenta = new CuentaBancaria("ES3333333333", 0.0);

        Operacion op = mock(Operacion.class);

        cuenta.addOperacion(op);
        cuenta.addOperacion(op); // mismo objeto -> no duplicar

        assertEquals(1, cuenta.operaciones.size());
    }

    @Test
    void testGetSaldoActualConOperacionNullEnLista() {
        CuentaBancaria cuenta = new CuentaBancaria("ES4444444444", 1000.0);

        Operacion op = mock(Operacion.class);
        when(op.getImporte()).thenReturn(250.0);

        cuenta.operaciones = new ArrayList<>();
        cuenta.operaciones.add(null); // verificar que se ignoran nulls en la suma
        cuenta.operaciones.add(op);

        double saldo = cuenta.getSaldoActual();

        assertEquals(1250.0, saldo);
        verify(op, times(1)).getImporte();
    }

    static class TestOperacion implements Operacion {
        private final String id;
        private final double importe;

        TestOperacion(String id, double importe) {
            this.id = id;
            this.importe = importe;
        }

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public String getConcepto() {
            return "";
        }

        @Override
        public double getImporte() {
            return importe;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestOperacion that = (TestOperacion) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }


}
