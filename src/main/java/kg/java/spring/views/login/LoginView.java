package kg.java.spring.views.login;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import kg.java.spring.core.config.AuthenticatedUser;

@AnonymousAllowed
@PageTitle("authentication")
@Route(value = "login")
@RouteAlias(value = "")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("L-center");
        i18n.getHeader().setDescription("Фитнес центр");
        i18n.getForm().setTitle("АВТОРИЗАЦИЯ");
        i18n.getForm().setUsername("Логин");
        i18n.getForm().setPassword("Пароль");
        i18n.getForm().setSubmit("ВОЙТИ");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            setOpened(false);
            var user = authenticatedUser.get().get();
            if (user.getRoles().toString().equals("[ADMIN]")) {
                event.forwardTo("admin_view");
            } else {
                event.forwardTo("user_view");
            }
        }
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
