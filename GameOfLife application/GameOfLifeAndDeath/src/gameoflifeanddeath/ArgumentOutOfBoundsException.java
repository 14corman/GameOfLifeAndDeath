/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameoflifeanddeath;

/**
 *
 * @author Cory Edwards
 */
public class ArgumentOutOfBoundsException extends Exception {

    /**
     * Creates a new instance of <code>ArgumentOutOfBoundsException</code>
     * without detail message.
     */
    public ArgumentOutOfBoundsException() {
    }

    /**
     * Constructs an instance of <code>ArgumentOutOfBoundsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ArgumentOutOfBoundsException(String msg) {
        super(msg);
    }
}
