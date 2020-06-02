package io.codeka.gaia.stacks.service;

import io.codeka.gaia.modules.bo.TerraformModule;
import io.codeka.gaia.modules.bo.Variable;
import io.codeka.gaia.modules.repository.TerraformModuleRepository;
import io.codeka.gaia.stacks.bo.Stack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegexStackVariablesValidatorTest {

    @Mock
    private TerraformModuleRepository moduleRepository;

    @InjectMocks
    private RegexStackVariablesValidator validator;

    @Test
    void validator_shouldWork_whenModuleIsNull(){
        assertThat(validator.isValid(new Stack(), null)).isTrue();
    }

    @Test
    void validator_shouldWork_whenModuleIsBlank(){
        var stack = new Stack();
        stack.setModuleId("     ");

        assertThat(validator.isValid(stack, null)).isTrue();
    }

    @Test
    void validator_shouldNotCheckNullVariables(){
        var variable = new Variable("test");
        variable.setValidationRegex("\\d{3,6}");

        var module = new TerraformModule();
        module.setVariables(List.of(variable));

        when(moduleRepository.findById("12")).thenReturn(Optional.of(module));

        var stack = new Stack();
        stack.setModuleId("12");

        assertThat(validator.isValid(stack, null)).isTrue();
    }

    @Test
    void validator_shouldNotCheckBlankVariables(){
        var variable = new Variable("test");
        variable.setValidationRegex("\\d{3,6}");

        var module = new TerraformModule();
        module.setVariables(List.of(variable));

        when(moduleRepository.findById("12")).thenReturn(Optional.of(module));

        var stack = new Stack();
        stack.setModuleId("12");
        stack.setVariableValues(Map.of("test", "     "));

        assertThat(validator.isValid(stack, null)).isTrue();
    }

    @Test
    void validator_shouldNotCheckNonRegexVariables(){
        var variable = new Variable("test");

        var module = new TerraformModule();
        module.setVariables(List.of(variable));

        when(moduleRepository.findById("12")).thenReturn(Optional.of(module));

        var stack = new Stack();
        stack.setModuleId("12");
        stack.setVariableValues(Map.of("test", "value"));

        assertThat(validator.isValid(stack, null)).isTrue();
    }

    @Test
    void validator_shouldReturnFalse_whenMandatoryVariableWithRegexDoesNotMatch(){
        var variable = new Variable("test");
        variable.setValidationRegex("\\d{3,6}");

        var module = new TerraformModule();
        module.setVariables(List.of(variable));

        when(moduleRepository.findById("12")).thenReturn(Optional.of(module));

        var stack = new Stack();
        stack.setModuleId("12");
        stack.setVariableValues(Map.of("test", "value"));

        assertThat(validator.isValid(stack, null)).isFalse();
    }

    @Test
    void validator_shouldReturnTrue_whenMandatoryVariableWithRegexMatches(){
        var variable = new Variable("test");
        variable.setValidationRegex("\\d{3,6}");

        var module = new TerraformModule();
        module.setVariables(List.of(variable));

        when(moduleRepository.findById("12")).thenReturn(Optional.of(module));

        var stack = new Stack();
        stack.setModuleId("12");
        stack.setVariableValues(Map.of("test", "1234"));

        assertThat(validator.isValid(stack, null)).isTrue();
    }

}
