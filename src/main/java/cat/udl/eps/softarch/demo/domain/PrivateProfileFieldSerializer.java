package cat.udl.eps.softarch.demo.domain;

import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * Shows private fields to their owner and administrators. Optional details can also be
 * shown on public profiles when the owner enables their individual visibility settings.
 */
public class PrivateProfileFieldSerializer extends ValueSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializationContext ctxt) {
        // The current value of the generator is the User being serialized or, in Spring Data
        // REST (HAL) responses, the EntityModel that wraps it
        Object current = gen.currentValue();
        Object profile = current instanceof EntityModel<?> model ? model.getContent() : current;
        if (canSeePrivateFields(profile) || (profile instanceof User user
                && user.isProfilePublic() && isPublicField(user))) {
            ctxt.writeValue(gen, value);
        } else {
            gen.writeNull();
        }
    }

    protected boolean isPublicField(User profile) {
        return false;
    }

    public static class FullName extends PrivateProfileFieldSerializer {
        @Override
        protected boolean isPublicField(User profile) {
            return profile.getShowFullName();
        }
    }

    public static class Email extends PrivateProfileFieldSerializer {
        @Override
        protected boolean isPublicField(User profile) {
            return profile.getShowEmail();
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
