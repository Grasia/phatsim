/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package phat.commands;

/**
 *
 * @author pablo
 * @param <T>
 */
public class PHATParameter<T>  {
    String name;
    T value;
    
    public PHATParameter(String name, T value) {
        this.name = name;
        this.value = value;
    }
    
    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
