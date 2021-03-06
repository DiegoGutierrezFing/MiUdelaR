package diego.com.miudelar.api.web;

import java.util.List;

import diego.com.miudelar.data.api.model.DtCalificaciones;
import diego.com.miudelar.data.api.model.DtCarrera;
import diego.com.miudelar.data.api.model.DtCurso;
import diego.com.miudelar.data.api.model.DtExamen;
import diego.com.miudelar.data.api.model.InscripcionCarreraBody;
import diego.com.miudelar.data.api.model.InscripcionCursoBody;
import diego.com.miudelar.data.api.model.InscripcionExamenBody;
import diego.com.miudelar.data.api.model.LoginBody;
import diego.com.miudelar.data.api.model.TokenFirebaseBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

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

    @POST("estudiante/token/")
    Call<String> enviarTokenFirebase(@Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Body TokenFirebaseBody tokenFirebaseBody);

    @GET("director/carrera/")
    public Call<List<DtCarrera>> getAllCarreras(@Header("Authorization") String authorization);

    @GET("estudiante/curso/{cedula}/")
    public Call<List<DtCurso>> getCursosByCedula(@Header("Authorization") String authorization, @Path("cedula") String cedula);

    @GET("estudiante/examen/{cedula}/")
    public Call<List<DtExamen>> getExamenesByCedula(@Header("Authorization") String authorization, @Path("cedula") String cedula);

    @GET("estudiante/consultarCalificaciones/{cedula}/")
    public Call<DtCalificaciones> getCalificacionesSAsig(@Header("Authorization") String authorization,
                                                    @Path("cedula") String cedula);

    @GET("estudiante/consultarCalificaciones/{cedula}/{idAsig_Carrera}/")
    public Call<DtCalificaciones> getCalificaciones(@Header("Authorization") String authorization,
                                                    @Path("cedula") String cedula,
                                                    @Path("idAsig_Carrera") Long idAsig_Carrera);

    @POST("estudiante/inscripcionCarrera/")
    public Call<String> inscripcionCarrera(@Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Body InscripcionCarreraBody inscripcionCarreraBody);

    @POST("estudiante/inscripcionCurso/")
    public Call<String> inscripcionCurso(@Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Body InscripcionCursoBody inscripcionCursoBody);

    @POST("estudiante/inscripcionExamen/")
    public Call<String> inscripcionExamen(@Header("Authorization") String authorization, @Header("Content-Type") String contentType, @Body InscripcionExamenBody inscripcionExamenBody);

}
