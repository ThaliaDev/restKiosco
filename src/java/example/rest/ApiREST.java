package example.rest;
import DAO.Kiosco;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Date;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * @see https://www.jc-mouse.net/
 * @author mouse
 */

@Path("/restapi")
public class ApiREST {
    
    @GET
    @Path("/rnd")
    @Produces(MediaType.APPLICATION_JSON)    
    /**
     * genera y retorna un numero aleatorio
     * @return Response
     */
    public Response generateRndNumber(){
        Random rnd = new Random();        
        return Response.ok(
                response("Numero Aleatorio", "", String.valueOf(rnd.nextDouble())), 
                MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("/fibo/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * Sucesion de fibonacci
     * @param value numero entero
     * @return Response
     */
    public Response getFibo(@PathParam("value") int value) {
        if(value<=0){                        
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(
                    response("Fibonacci", String.valueOf(value), "El numero debe ser mayor que cero")).build();
        }               
        int fibo1 = 1;
        int fibo2 = 1;
        int aux = 1;
        String cadena = "1";
        for (int i = 2; i <= value; i++) {
            fibo2 += aux;
            aux = fibo1;
            fibo1 = fibo2;
            cadena += " " + aux;
        }        
        return Response.ok(
                response("Fibonacci", String.valueOf(value), cadena), 
                MediaType.APPLICATION_JSON).build();
    }
    
    @GET
    @Path("/restKiosco/validarusuario/{usuario}/{clave}/{nit}/{grupo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getvalidarUsuarioClave(@PathParam("usuario") String usuario, @PathParam("clave") String clave, @PathParam("nit") String nit, @PathParam("grupo") String grupo){
        Kiosco k=new Kiosco();
        Boolean r=k.validarUsuarioClave(usuario, clave);
        String[] parametros={usuario, clave};
        if (r==true) {
            return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();
        }else{
            ////Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    
    @GET
    @Path("/restKiosco/jwt/{usuario}/{clave}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJWT(@PathParam("usuario") String usuario, @PathParam("clave") String clave){
        Kiosco k=new Kiosco();
        Boolean r=k.validarUsuarioClave(usuario, clave);
        String[] parametros={usuario, clave};
        if (r==true) {
            long tiempo=System.currentTimeMillis();
            String jwt=Jwts.builder()
                    .signWith(SignatureAlgorithm.HS256, usuario)
                    .setSubject("nombre Usuario")
                    .setIssuedAt(new Date(tiempo))
                    .setExpiration(new Date(tiempo+900000))
                    .claim("email", "email....")
                    .compact();
                    
            JsonObject json=Json.createObjectBuilder()
                    .add("JWT", jwt).build();
            /*return Response.ok(
                response("ValidarUsuarioYClave", "Usuario: "+usuario+", Clave: "+clave, String.valueOf(r)), MediaType.APPLICATION_JSON).build();*/
            return Response.status(Response.Status.CREATED).entity(json).build();
            // Usuario prueba exitosa: http://localhost:8080/wsjavanov5/jcmouse/restapi/restKiosco/jwt/53065192/Lore0204*
        }else{
            ////Error cod 401
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    /**
     * metodo privado para dar formato al JSON de respuesta
     * @param operation Operacion que se realiza en el APIREST
     * @param paramater parametro de entrada
     * @param result resultado de la operacion realizada
     * @return String Respuesta en formato JSON
     */
    private String response(String operation, String parameter, String result) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("operation", operation);
            obj.put("parameter", parameter);
            obj.put("result", result);            
            return obj.toString(4);
        } catch (JSONException ex) {
            System.err.println("JSONException: " + ex.getMessage());
        }
        return "";
    }
    
}