package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.TestPreview;

public class TestPreviewOperationsImpl implements TestPreviewOperations {
    @Override
    public <S extends TestPreview> S save(S preview) {
        // Spring Data REST needs an identifier to render a 201 response. Previews are not stored.
        preview.setId(0L);
        return preview;
    }
}
