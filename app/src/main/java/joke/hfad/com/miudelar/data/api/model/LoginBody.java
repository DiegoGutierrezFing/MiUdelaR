package joke.hfad.com.miudelar.data.api.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * POJO para representar el cuerpo de la petición POST para el login
 */
public class LoginBody implements Serializable {
    @SerializedName("username")
    @Expose
    private String userId;
    @SerializedName("password")
    @Expose
    private String password;

    public LoginBody(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
