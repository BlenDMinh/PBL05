package modules.ruleset.service;

import java.util.List;

import com.google.gson.JsonObject;

import modules.ruleset.RuleSetRepository;
import modules.ruleset.dto.RuleSetDto;

public class RuleSetService {
    RuleSetRepository ruleSetRepository = new RuleSetRepository();

    public List<RuleSetDto> getAllRuleSet(boolean published) {
        return ruleSetRepository.getAllRuleSet(published);
    }

    public List<RuleSetDto> getAllRuleSet() {
        return ruleSetRepository.getAllRuleSet();
    }

    public RuleSetDto getById(int id) {
        return ruleSetRepository.getById(id);
    }

    public RuleSetDto getPublishedRuleSetById(int id) {
        return ruleSetRepository.getById(id, true);
    }

    public boolean addRuleSet(String name, JsonObject detail, JsonObject description) {
        return ruleSetRepository.addOne(name, detail.toString(), description != null ? description.toString() : null);
    }

    public boolean updateRuleSet(int id, String name, JsonObject detail, JsonObject description, boolean published) {
        return ruleSetRepository.udpateRuleSet(id, name, detail.toString(),
                description != null ? description.toString() : null, published);
    }

    public boolean deleteRuleSet(int id) {
        return ruleSetRepository.deleteOne(id);
    }
}
