/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joke.hfad.com.miudelar.data.api.model;

/**
 *
 * @author rmoreno
 */
public enum DiaSemana {
    DOMINGO("Domingo"),
    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miércoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sábado");
    
    private String diaString;
    
    DiaSemana(String diaString){
        this.diaString = diaString;
    }
    
    public String diaString() {
        return diaString;
    }
}
