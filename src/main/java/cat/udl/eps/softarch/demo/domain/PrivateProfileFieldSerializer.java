package cat.udl.eps.softarch.demo.domain;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Serializes a private profile field (email, full name, birth date, role...) only when the
 * viewer is the owner of the profile or an administrator; anyone else receives null (2.4.7).
 * Until persistent privacy preferences exist, these fields are never public.
 */
public class PrivateProfileFieldSerializer extends ValueSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt) {
        // The current value of the generator is the User being serialized or, in Spring Data
        // REST (HAL) responses, the EntityModel that wraps it
        Object current = gen.currentValue();
        Object profile = current instanceof EntityModel<?> model ? model.getContent() : current;
        if (canSeePrivateFields(profile)) {
            ctxt.writeValue(gen, value);
        } else {
            gen.writeNull();
        }
    }

    static boolean canSeePrivateFields(Object profile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User viewer)) {
            return false;
        }
        if (viewer.getRole() == User.Role.ADMIN) {
            return true;
        }
        return profile instanceof User owner
                && owner.getId() != null
                && owner.getId().equals(viewer.getId());
    }
}
