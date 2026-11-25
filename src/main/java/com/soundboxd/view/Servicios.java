package com.soundboxd.view;

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
    private static final ListaService LISTAS = new ListaServiceFalso();
    private static final SocialService SOCIAL = new SocialServiceFalso();
    private static final ModeracionService MODERACION = new ModeracionServiceFalso();

    private Servicios() { }

    public static AuthService auth() { return AUTH; }

    public static CatalogoService catalogo() { return CATALOGO; }

    public static ResenaService resenas() { return RESENAS; }

    public static ListaService listas() { return LISTAS; }

    public static SocialService social() { return SOCIAL; }

    public static ModeracionService moderacion() { return MODERACION; }
}
