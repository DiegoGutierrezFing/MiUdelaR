package joke.hfad.com.miudelar.data.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO para representar el cuerpo de la petición POST para la inscripcion a carrera
 */
public class InscripcionCarreraBody implements Serializable {
    @SerializedName("cedula")
    @Expose
    private String userId;
    @SerializedName("codigo")
    @Expose
    private Long codigo;

    public InscripcionCarreraBody(String userId, Long codigo) {
        this.userId = userId;
        this.codigo = codigo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
}
