package diego.com.miudelar.data.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO para representar el cuerpo de la petición POST para la inscripcion a curso
 */
public class InscripcionCursoBody implements Serializable {

    @SerializedName("cedula")
    @Expose
    private String userId;

    @SerializedName("idCurso")
    @Expose
    private Long idCurso;

    public InscripcionCursoBody(String userId, Long idCurso) {
        this.userId = userId;
        this.idCurso= idCurso;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso= idCurso;
    }
}
