package school.faang.user_service.parser;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.generated.Person;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PersonCsvParser {

    public MappingIterator<Person> parse(InputStream csv) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema csvSchema = csvMapper.schemaFor(Person.class).withHeader().withColumnReordering(true);
        return csvMapper.readerFor(Person.class).with(csvSchema).readValues(csv);
    }
}