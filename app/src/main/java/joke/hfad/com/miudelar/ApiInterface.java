package joke.hfad.com.miudelar;

import java.util.List;

import joke.hfad.com.miudelar.data.api.model.DtCarrera;
import joke.hfad.com.miudelar.data.api.model.LoginBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    /*@GET("/albums/{id}")
    public Call<Album> getAlbumWithID(@Path("id") int id);

    @GET("/albums/")
    public Call<List<Album>> getAllAlbums();

    @POST("/albums")
    public Call<Album> albumData(@Body Album data);*/

    /*@GET("/admin/usuario")
    public Call<List<DtUsuario>> getAllUsuarios();*/

    /*@GET("director/carrera/")
    public Call<List<DtCarrera>> getAllCarreras();*/

    @POST("admin/login/")
    Call<String> login(@Body LoginBody loginBody);

    @GET("director/carrera/")
    public Call<List<DtCarrera>> getAllCarreras(@Header("Authorization") String authorization);

    @POST("estudiante/inscripcionCarrera/")
    public Call<String> inscripcionCarrera(@Header("Authorization") String authorization, @Body String inscripcionCarreraDatos);

    @POST("estudiante/inscripcionCurso/")
    public Call<String> inscripcionCurso(@Header("Authorization") String authorization, @Body String inscripcionCursoDatos);

    @POST("estudiante/inscripcionExamen/")
    public Call<String> inscripcionExamen(@Header("Authorization") String authorization, @Body String inscripcionExamenDatos);


    /*@POST("/albums")
    public Call<Album> albumData(@Body Album data);*/
}
