package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entiity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashGeneratorService {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final HashRepository hashRepository;

    @Transactional
    public void generateHashesInternal(List<Long> sequences) {
        if (sequences.isEmpty()) {
            log.warn("No sequence values were generated!");
            return;
        }

        log.info("Sample sequence values: first={}, last={}",
                sequences.get(0), sequences.get(sequences.size() - 1));

        List<Hash> hashes = sequences.stream()
                .map(seq -> {
                    String hash = base62Encode(seq);
                    log.debug("Generated hash {} for sequence {}", hash, seq);
                    return new Hash(hash);
                })
                .collect(toList());

        log.info("Generated {} hashes, saving to database", hashes.size());
        var savedHashes = hashRepository.saveAll(hashes);
        log.info("Successfully saved {} hashes to database", savedHashes.size());
    }

    private String base62Encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}