package com.prokarma.oneclick.stacks.service

import com.prokarma.oneclick.modules.repository.TerraformModuleRepository
import com.prokarma.oneclick.stacks.bo.MandatoryStackVariablesValidation
import com.prokarma.oneclick.stacks.bo.Stack
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

@Service
class MandatoryStackVariablesValidator(@Autowired val moduleRepository: TerraformModuleRepository)
    : ConstraintValidator<com.prokarma.oneclick.stacks.bo.MandatoryStackVariablesValidation, com.prokarma.oneclick.stacks.bo.Stack> {

    override fun isValid(stack: com.prokarma.oneclick.stacks.bo.Stack, constraintValidatorContext: ConstraintValidatorContext?): Boolean {
        // we do not validate anything if the stacks has no module reference (it will send a BadRequest anyway)
        if (stack.moduleId.isNullOrBlank()) {
            return true
        }
        // getting the module, returning true if module cannot be found !
        // this should (let's hope so) never happen ! another validator should handle this case
        val module = this.moduleRepository.findByIdOrNull(stack.moduleId) ?: return true

        return module.variables.filter { it.mandatory }
                .filter { it.defaultValue.isNullOrBlank() }
                .all { ! stack.variableValues[it.name].isNullOrBlank() }
    }
}
