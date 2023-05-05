package kg.java.spring.core.service.impl;

import kg.java.spring.core.model.entity.SeasonCard;
import kg.java.spring.core.repository.SeasonCardRepository;
import kg.java.spring.core.service.SeasonCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeasonCardServiceImpl implements SeasonCardService {
    private final SeasonCardRepository seasonCardRepository;

    @Override
    public List<SeasonCard> getSeasonCards() {
        return seasonCardRepository.findAll();
    }

    @Override
    public SeasonCard save(SeasonCard seasonCard) {
        return seasonCardRepository.save(seasonCard);
    }
}
