package cat.udl.eps.softarch.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	DataSource dataSource;

	@Test
	void contextLoads() {
	}

	@Test
	void recordTimestampColumnsIndicateUtc() throws Exception {
		Set<String> columns = new HashSet<>();
		try (Connection connection = dataSource.getConnection();
			 ResultSet result = connection.getMetaData().getColumns(null, null, "RECORD", null)) {
			while (result.next()) {
				columns.add(result.getString("COLUMN_NAME"));
			}
		}
		assertThat(columns).contains("CREATED_UTC", "MODIFIED_UTC")
				.doesNotContain("CREATED", "MODIFIED");
	}

}
