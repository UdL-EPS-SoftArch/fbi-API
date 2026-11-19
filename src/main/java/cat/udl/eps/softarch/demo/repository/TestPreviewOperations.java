package cat.udl.eps.softarch.demo.repository;

import cat.udl.eps.softarch.demo.domain.TestPreview;

public interface TestPreviewOperations {
    <S extends TestPreview> S save(S preview);
}
