package com.prokarma.oneclick.modules.controller;

import com.prokarma.oneclick.modules.bo.Variable;
import com.prokarma.oneclick.modules.repository.TerraformModuleGitRepository;
import com.prokarma.oneclick.modules.repository.TerraformModuleRepository;
import com.prokarma.oneclick.modules.bo.TerraformModule;
import com.prokarma.oneclick.modules.repository.TerraformModuleGitRepository;
import com.prokarma.oneclick.modules.repository.TerraformModuleRepository;
import com.prokarma.oneclick.modules.util.ModuleUtil;
import com.prokarma.oneclick.stacks.bo.Stack;
import com.prokarma.oneclick.stacks.repository.JobRepository;
import com.prokarma.oneclick.stacks.repository.StackRepository;
import com.prokarma.oneclick.teams.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Rest controller for the module API
 */
@RestController
@RequestMapping("/api/modules")
@Secured({"ROLE_USER","ROLE_ADMIN"})
public class ModuleRestController {

    private static  final String GOOGLE_PROVIDER = "GOOGLE";
    private static  final String GOOGLE_PROVIDER_CRED_FORMAT = "${file(\"%s\")}";
    private static  final String CRED_VAR_KEY = "credentials";

    private TerraformModuleRepository moduleRepository;

    private TerraformModuleGitRepository moduleGitRepository;

    private StackRepository stackRepository;

    private JobRepository jobRepository;

    @Value("${cloud.credentials.path}")
    private String credentialsLocation;

    @Autowired
    public ModuleRestController(TerraformModuleRepository moduleRepository, TerraformModuleGitRepository moduleGitRepository,
                                StackRepository stackRepository, JobRepository jobRepository) {
        this.moduleRepository = moduleRepository;
        this.moduleGitRepository = moduleGitRepository;
        this.stackRepository = stackRepository;
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<TerraformModule> findAllModules(User user){
        if(user.isAdmin()){
            return moduleRepository.findAll();
        }
        if(user.getTeam() != null){
            return moduleRepository.findAllByModuleMetadataCreatedByOrAuthorizedTeamsContaining(user, user.getTeam());
        }
        return moduleRepository.findAllByModuleMetadataCreatedBy(user);
    }

    @GetMapping("/{id}")
    public TerraformModule findModule(@PathVariable String id, User user){
        var module = moduleRepository.findById(id).orElseThrow(ModuleNotFoundException::new);
        if(!module.isAuthorizedFor(user)){
            throw new ModuleForbiddenException();
        }
        return module;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TerraformModule createModule(@RequestBody TerraformModule module, User user){
        module.setId(UUID.randomUUID().toString());
        module.getModuleMetadata().setCreatedBy(user);
        if (GOOGLE_PROVIDER.equalsIgnoreCase(module.getMainProvider())) {
            String credentialsFile = ModuleUtil.addSecretFile(module, credentialsLocation);
            Variable variable =new Variable(CRED_VAR_KEY, null, null, String.format(GOOGLE_PROVIDER_CRED_FORMAT, credentialsFile),
                    false, false, null);
            module.getVariables().add(variable);
        }
        return moduleRepository.save(module);
    }

    @PutMapping("/{id}")
    public TerraformModule saveModule(@PathVariable String id, @RequestBody @Valid TerraformModule module, User user){
        var existingModule = moduleRepository.findById(id).orElseThrow(ModuleNotFoundException::new);
        if(!existingModule.isAuthorizedFor(user)){
            throw new ModuleForbiddenException();
        }

        module.getModuleMetadata().setUpdatedBy(user);
        module.getModuleMetadata().setUpdatedAt(LocalDateTime.now());
        if (GOOGLE_PROVIDER.equalsIgnoreCase(module.getMainProvider())) {
            String credentialsFile = ModuleUtil.addSecretFile(module, credentialsLocation);
            Variable variable =new Variable(CRED_VAR_KEY, null, null, String.format(GOOGLE_PROVIDER_CRED_FORMAT, credentialsFile),
                    false, false, null);
            module.getVariables().add(variable);
        }

        return moduleRepository.save(module);
    }

    @GetMapping("/{id}/readme")
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> readme(@PathVariable String id) {
        var module = moduleRepository.findById(id).orElseThrow();
        return moduleGitRepository.getReadme(module);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteModule(@PathVariable String id, User user){
        var module = moduleRepository.findById(id).orElseThrow(ModuleNotFoundException::new);
        if(!module.isAuthorizedFor(user)){
            throw new ModuleForbiddenException();
        }
        List<Stack> stackList = stackRepository.findByModuleId(id);
        for (Stack stack : stackList) {
            jobRepository.deleteByStackId(stack.getId());
            stackRepository.deleteById(stack.getId());
        }

        moduleRepository.deleteById(id);
    }

}

@ResponseStatus(HttpStatus.NOT_FOUND)
class ModuleNotFoundException extends RuntimeException{
}

@ResponseStatus(HttpStatus.FORBIDDEN)
class ModuleForbiddenException extends RuntimeException{
}

