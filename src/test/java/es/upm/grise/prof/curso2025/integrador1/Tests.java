// java
package es.upm.grise.prof.curso2025.integrador1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
