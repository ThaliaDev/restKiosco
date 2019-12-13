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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Soporte
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
            System.out.println("Error: "+e.getStackTrace());
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
    
    public static void main(String[] args) {
        new Kiosco().getConexion();
    }
    
    
}
