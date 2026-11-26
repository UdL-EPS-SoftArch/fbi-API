package cat.udl.eps.softarch.demo.domain;

import org.springframework.hateoas.EntityModel;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ProfileVisibilitySerializer extends ValueSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator generator, SerializationContext context) {
        Object current = generator.currentValue();
        Object profile = current instanceof EntityModel<?> model ? model.getContent() : current;
        if (profile instanceof User user && (user.isProfilePublic() || PrivateProfileFieldSerializer.canSeePrivateFields(user))) {
            context.writeValue(generator, value);
        } else {
            generator.writeNull();
        }
    }
}
