package diego.com.miudelar.data.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * POJO para representar el cuerpo de la petición POST para el token de usuario en firebase
 */
public class TokenFirebaseBody implements Serializable {
    @SerializedName("cedula")
    @Expose
    private String userId;
    @SerializedName("token")
    @Expose
    private String token;

    public TokenFirebaseBody(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
