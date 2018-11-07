package diego.com.miudelar.data.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO para representar el cuerpo de la petición POST para la inscripcion a examen
 */
public class InscripcionExamenBody implements Serializable {

    @SerializedName("cedula")
    @Expose
    private String userId;

    @SerializedName("idExamen")
    @Expose
    private Long idExamen;

    public InscripcionExamenBody(String userId, Long idExamen) {
        this.userId = userId;
        this.idExamen = idExamen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getIdExamen() {
        return idExamen;
    }

    public void setIdExamen(Long idExamen) {
        this.idExamen= idExamen;
    }
}
