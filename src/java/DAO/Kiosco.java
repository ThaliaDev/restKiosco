/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;


/**
 *
 * @author Thalia Manrique
 */
public class Kiosco {
    
    static Connection con=null;
    
    public Kiosco(){

    }
    
    Connection getConexion(){
        try {
            if (this.con==null) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                this.con=DriverManager.getConnection("jdbc:oracle:thin:@nominasaas2:1521:soporte", "MTBASE", "MTBASE");
                System.out.println("Conectado");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getStackTrace());
        }  
        return this.con;
    }
    
    public boolean validarUsuarioClave(String usuario, String clave){
        boolean res=false;
        try {
            PreparedStatement ps=getConexion().prepareStatement("select count(*) as total from conexioneskioskos ck, empleados e where ck.empleado=e.secuencia "
            + " and e.codigoempleado=? and ck.pwd=generales_pkg.encrypt(?) ");
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("total")>0){
                    res=true;
                }else{
                    res=false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error DAO.Kiosco.validarUsuarioClave() "+e.getMessage());
            res=false;
        }
        return res;
    }
    
    
        public boolean validarLogin(String codEmple, String clave, String nit){
        boolean res=false;
        try {
            String sql="select count(*) as total " +
            "from conexioneskioskos ck, empleados e, empresas em " +
            "where " +
            "ck.empleado=e.secuencia " +
            "and e.empresa=em.secuencia " +
            "and e.codigoempleado= ? " +
            "and ck.pwd=generales_pkg.encrypt(?) " +
            "and em.nit= ? and empleadocurrent_pkg.tipotrabajadorcorte(e.secuencia, sysdate)='ACTIVO'";
            System.out.println(sql);
            System.out.println("Nit: "+nit+" clave: "+clave+" usuario: "+codEmple);
            PreparedStatement ps=getConexion().prepareStatement(sql);
             ps.setString(1, codEmple);
             ps.setString(2, clave);
             ps.setString(3, nit);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                System.out.println("total: "+rs.getInt("total"));
                System.out.println(rs.getInt(1));
                if(rs.getInt("total")>0){
                    res=true;
                }else{
                    res=false;
                }
            }
        } catch (Exception e) {
            System.out.println("Error DAO.Kiosco.validarLogin() "+e.getMessage()+" "+e.getLocalizedMessage());
            res=false;
        }
        return res;
    }
    
    
        public String getDatosEmpleadoNit(String codEmpleado, String nit){
        String datos=null;
        JSONObject obj = new JSONObject();
        /*String sql=
                "select " +
                "  e.codigoempleado usuario, " +
                "  p.nombre nombre, "+
                "  p.primerapellido apellido1," +
                "  p.segundoapellido apellido2, " +
                "  p.sexo sexo, " +
                "  p.FECHANACIMIENTO fechaNacimiento, " +
                "  (select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento, " +
                "  p.GRUPOSANGUINEO grupoSanguineo, " +
                "  p.FACTORRH factorRH, " +
                "  (select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu, " +
                "  p.NUMERODOCUMENTO documento, " +
                "  (select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu, " +
                "  p.EMAIL email, " +
                "  'DIRECCION' direccion, " +
                "  ck.ULTIMACONEXION ultimaConexion, " +
                "  em.nit nitEmpresa, " +
                "  em.nombre nombreEmpresa, " +
                "  empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato, " +
                "  empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, sysdate) salario, " +
                "  empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo, " +
                "  DIAS360(empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate), sysdate) diasW, " +
                "  empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual, " +
                    "  (select sum(V.diaspendientes) " +
                        "from " +
                        "empleados ei, " +
                        "VWVACAPENDIENTESEMPLEADOS V " +
                        "where V.EMPLEADO(+)=Ei.SECUENCIA " +
                        "AND Ei.SECUENCIA =  e.secuencia " +
                        "AND V.FINALCAUSACION < sysdate) diasVacaPendientes, " +
                "  empleadocurrent_pkg.empresaempleado(e.secuencia, sysdate) ciudadEmpleado "+
                "  from " +
                "  empleados e, conexioneskioskos ck, empresas em, personas p " +
                "  where " +
                "  e.persona=p.secuencia " +
                "  and e.empresa=em.secuencia " +
                "  and ck.empleado=e.secuencia " +
                "  and e.codigoempleado= ? " +
                "  and em.nit= ? ";*/
        String sql="select  \n" +
        "  e.codigoempleado usuario,  \n" +
        "  p.nombre nombres, +\n" +
        "  p.primerapellido apellido1,\n" +
        "  p.segundoapellido apellido2,  \n" +
        "  p.sexo sexo,  \n" +
        "  p.FECHANACIMIENTO fechaNacimiento,  \n" +
        "  (select nombre from ciudades where secuencia=p.CIUDADNACIMIENTO) ciudadNacimiento,  \n" +
        "  p.GRUPOSANGUINEO grupoSanguineo,  \n" +
        "  p.FACTORRH factorRH,  \n" +
        "  (select nombrelargo from tiposdocumentos where secuencia=p.TIPODOCUMENTO) tipoDocu,  \n" +
        "  p.NUMERODOCUMENTO documento,  \n" +
        "  (select nombre from ciudades where secuencia=p.CIUDADDOCUMENTO) lugarExpediDocu,  \n" +
        "  p.EMAIL email,  \n" +
        "  'DIRECCION' direccion,  \n" +
        "  ck.ULTIMACONEXION ultimaConexion,  \n" +
        "  em.codigo codigoEmpresa, "+
        "  em.nit nitEmpresa,  \n" +
        "  em.nombre nombreEmpresa,  \n" +
        "  empleadocurrent_pkg.descripciontipocontrato(e.secuencia, sysdate) contrato,  \n" +
        "  empleadocurrent_pkg.ValorBasicoCorte(e.secuencia, sysdate) salario,  \n" +
        "  empleadocurrent_pkg.DescripcionCargoCorte(e.secuencia, sysdate) cargo,  \n" +
        "  DIAS360(empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate), sysdate) diasW,  \n" +
        "  empleadocurrent_pkg.FechaVigenciaTipoContrato(e.secuencia, sysdate) inicioContratoActual,  \n" +
        "	  (select sum(V.diaspendientes)  \n" +
        "		from  \n" +
        "		empleados ei,  \n" +
        "		VWVACAPENDIENTESEMPLEADOS V  \n" +
        "		where V.EMPLEADO(+)=Ei.SECUENCIA  \n" +
        "		AND Ei.SECUENCIA =  e.secuencia  \n" +
        "		AND V.FINALCAUSACION < sysdate) diasVacaPendientes,  \n" +
        "  (select nombre from ciudades where secuencia=ug.CIUDAD) ciudadEmpleado, \n" +
        " (select nombre from estructuras where secuencia=empleadocurrent_pkg.estructuracargocorte(e.secuencia, sysdate)) estructura " +
        "  from  \n" +
        "  empleados e, conexioneskioskos ck, empresas em, personas p, vigenciasubicaciones vu, ubicacionesgeograficas ug  \n" +
        "  where  \n" +
        "  e.persona=p.secuencia  \n" +
        "  and e.empresa=em.secuencia  \n" +
        "  and ck.empleado=e.secuencia  \n" +
        "  and vu.empleado=e.secuencia\n" +
        "  AND vu.ubicacion = ug.secuencia\n" +
        "  AND vu.fechavigencia = (SELECT MAX(A.FECHAVIGENCIA)\n" +
        "                               FROM  vigenciasubicaciones a\n" +
        "                               WHERE empleado = e.secuencia\n" +
        "                               AND a.fechavigencia <= sysdate)\n" +
        "  and e.codigoempleado= ?  \n" +
        "  and em.nit= ?";
        try {
            PreparedStatement ps=getConexion().prepareStatement(sql);
            ps.setString(1, codEmpleado);
            ps.setString(2, nit);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                obj.put("usuario", rs.getString(1));
                obj.put("nombres", rs.getString("nombres"));
                obj.put("apellido1", rs.getString("apellido1"));
                obj.put("apellido2", rs.getString("apellido2"));
                obj.put("sexo", rs.getString("sexo"));
                obj.put("fechaNacimiento", rs.getString("fechaNacimiento"));
                obj.put("ciudadNacimiento", rs.getString("ciudadNacimiento"));
                obj.put("grupoSanguineo", rs.getString("grupoSanguineo"));
                obj.put("factorRH", rs.getString("factorRH"));
                obj.put("tipoDocu", rs.getString("tipoDocu"));
                obj.put("documento", rs.getString("documento"));
                obj.put("lugarExpediDocu", rs.getString("lugarExpediDocu"));
                obj.put("email", rs.getString("email"));
                obj.put("direccion", rs.getString("direccion"));
                obj.put("ultimaConexion", rs.getString("ultimaConexion"));
                obj.put("codigoEmpresa", rs.getString("codigoEmpresa"));
                obj.put("nitEmpresa", rs.getString("nitEmpresa"));
                obj.put("nombreEmpresa", rs.getString("nombreEmpresa"));
                obj.put("contrato", rs.getString("contrato"));
                obj.put("salario", rs.getString("salario"));
                obj.put("cargo", rs.getString("cargo"));
                obj.put("diasW", rs.getString("diasW"));
                obj.put("inicioContratoActual", rs.getString("inicioContratoActual"));
                obj.put("diasVacaPendientes", rs.getString("diasVacaPendientes"));
                obj.put("ciudadEmpleado", rs.getString("ciudadEmpleado"));
                obj.put("estructura", rs.getString("estructura"));
            }
        } catch (Exception e) {
            System.out.println("Error DAO.Kiosco.getDatosEmpleadoNit() "+e.getMessage());
            
        }
        return obj.toString();
    }
    
    public static void main(String[] args) {
        new Kiosco().getConexion();
    }
    
    
}
