package com.soundboxd.view;

import com.soundboxd.model.Album;
import com.soundboxd.security.AuthService;
import com.soundboxd.service.CatalogoService;
import com.soundboxd.service.ListaService;
import com.soundboxd.service.ModeracionService;
import com.soundboxd.service.ResenaService;
import com.soundboxd.service.SocialService;
import com.soundboxd.view.falso.AuthServiceFalso;
import com.soundboxd.view.falso.CatalogoServiceFalso;
import com.soundboxd.view.falso.ListaServiceFalso;
import com.soundboxd.view.falso.ModeracionServiceFalso;
import com.soundboxd.view.falso.ResenaServiceFalso;
import com.soundboxd.view.falso.SocialServiceFalso;

import java.util.List;

/**
 * Único lugar donde se crean los servicios que usan los controladores.
 *
 * AHORA devuelven versiones con datos falsos (paquete view.falso).
 * Cuando Amado y Bryan publiquen las implementaciones reales, solo se cambia la línea
 * que crea cada servicio; ningún controlador se toca. Por ejemplo:
 *     private static final CatalogoService CATALOGO = new CatalogoServiceImpl();   // nombre que use Amado
 *     private static final AuthService AUTH = new AuthService();                   // cuando B2 esté implementado
 */
public final class Servicios {

    private static final AuthService AUTH = new AuthServiceFalso();
    private static final CatalogoService CATALOGO = new CatalogoServiceFalso();
    private static final ResenaService RESENAS = new ResenaServiceFalso();
    private static final ListaServiceFalso LISTAS = new ListaServiceFalso();
    private static final SocialService SOCIAL = new SocialServiceFalso();
    private static final ModeracionService MODERACION = new ModeracionServiceFalso();

    private Servicios() { }

    public static AuthService auth() { return AUTH; }

    public static CatalogoService catalogo() { return CATALOGO; }

    public static ResenaService resenas() { return RESENAS; }

    public static ListaService listas() { return LISTAS; }

    /**
     * PENDIENTE-AMADO: ListaService no tiene un método para obtener los álbumes de una lista.
     * Mientras tanto se usa el del servicio falso. Cuando Amado lo agregue a la interfaz,
     * cambiar el cuerpo por {@code return LISTAS.listarAlbumes(idLista);} con el servicio real.
     */
    public static List<Album> albumesDeLista(int idLista) { return LISTAS.listarAlbumes(idLista); }

    public static SocialService social() { return SOCIAL; }

    public static ModeracionService moderacion() { return MODERACION; }
}
