/**
 * This file was generated by the JPA Modeler
 */
package diego.com.miudelar.data.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class DtCurso implements Serializable {

    private Long id;

    private Date fecha;

    private DtAsignatura_Carrera asignatura_Carrera;

    private List<DtHorario> horarios;

    private List<DtEstudiante_Curso> calificacionesCursos;

    private List<DtUsuario> inscriptos;

    public DtCurso() {
    }

    public DtCurso(Long id, Date fecha, DtAsignatura_Carrera asignatura_Carrera, List<DtHorario> horarios) {
        this.id = id;
        this.fecha = fecha;
        this.asignatura_Carrera = asignatura_Carrera;
        this.horarios = horarios;
    }

    public DtCurso(Long id, Date fecha, DtAsignatura_Carrera asignatura_Carrera, List<DtHorario> horarios, List<DtEstudiante_Curso> calificacionesCursos, List<DtUsuario> inscriptos) {
        this.id = id;
        this.fecha = fecha;
        this.asignatura_Carrera = asignatura_Carrera;
        this.horarios = horarios;
        this.calificacionesCursos = calificacionesCursos;
        this.inscriptos = inscriptos;
    }

    public DtCurso(Date fecha, DtAsignatura_Carrera asignatura_Carrera) {
        this.fecha = fecha;
        this.asignatura_Carrera = asignatura_Carrera;
    }
        
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public DtAsignatura_Carrera getAsignatura_Carrera() {
        return asignatura_Carrera;
    }

    public void setAsignatura_Carrera(DtAsignatura_Carrera asignatura_Carrera) {
        this.asignatura_Carrera = asignatura_Carrera;
    }

    public List<DtHorario> getHorarios() {
        return this.horarios;
    }

    public void setHorarios(List<DtHorario> horarios) {
        this.horarios = horarios;
    }

    public List<DtEstudiante_Curso> getCalificacionesCursos() {
        return this.calificacionesCursos;
    }

    public void setCalificacionesCursos(List<DtEstudiante_Curso> calificacionesCursos) {
        this.calificacionesCursos = calificacionesCursos;
    }

    public List<DtUsuario> getInscriptos() {
        return this.inscriptos;
    }

    public void setInscriptos(List<DtUsuario> inscriptos) {
        this.inscriptos = inscriptos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DtCurso other = (DtCurso) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    
}
