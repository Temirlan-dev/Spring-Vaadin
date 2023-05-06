package kg.java.spring.core.service;

import kg.java.spring.core.model.entity.SeasonCard;
import java.util.List;

public interface SeasonCardService {
    List<SeasonCard> getSeasonCards();
    SeasonCard save(SeasonCard seasonCard);
}
