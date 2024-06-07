package faang.school.postservice.service.spelling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.json.SpellingCorrector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpellingServiceImplTest {

    @InjectMocks
    private SpellingServiceImpl spellingService;
    @Mock
    private RestTemplate restTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    private String json;
    private String string;

    @BeforeEach
    public void init(){
        SpellingCorrector firstSpell = SpellingCorrector.builder()
                .word("синхрафазатрон")
                .possibleVariants(new String[]{"синхрофазотрон", "синхрофазотрона"})
                .build();
        SpellingCorrector secondSpell = SpellingCorrector.builder()
                .word("Дубна")
                .possibleVariants(new String[]{"Дубне"})
                .build();
        try {
            json = objectMapper.writeValueAsString(new SpellingCorrector[]{firstSpell, secondSpell});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        string = "синхрафазатрон в Дубна";
    }

    @Test
    public void testCheckSpellingWithCorrecting() throws Exception{
        byte[] byteJson = json.getBytes(StandardCharsets.UTF_8);
        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(byteJson);

        CompletableFuture<Optional<String>> result = spellingService.checkSpelling(string);

        InOrder inOrder = inOrder(restTemplate, objectMapper);
        inOrder.verify(restTemplate, times(1)).getForObject(anyString(), eq(byte[].class));
        inOrder.verify(objectMapper, times(1)).readValue(any(byte[].class), eq(SpellingCorrector[].class));
        assertFalse(result.get().isEmpty());
        assertEquals(result.get().get(),"синхрофазотрон в Дубне" );
    }

    @Test
    public void testCheckSpellingWithEmptyResult() throws Exception{
        json = objectMapper.writeValueAsString(new SpellingCorrector[]{});
        byte[] byteJson = json.getBytes(StandardCharsets.UTF_8);
        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(byteJson);

        CompletableFuture<Optional<String>> result = spellingService.checkSpelling(string);
        assertTrue(result.get().isEmpty());
    }

    @Test
    public void testCheckSpellingWithIOException(){
        byte[] byteJson = "".getBytes(StandardCharsets.UTF_8);
        when(restTemplate.getForObject(anyString(), eq(byte[].class))).thenReturn(byteJson);

        var exception = assertThrows(RuntimeException.class, ()-> spellingService.checkSpelling(string));
        assertEquals(exception.getMessage(), "Error converting JSON to POJO");
    }
}
