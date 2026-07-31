package org.cdc.generator.utils.decorators;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.cdc.generator.utils.WorkspaceUtils;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WorkspaceDecorator {

    private static final HashMap<Workspace,WorkspaceDecorator> workspaceWorkspaceDecoratorHashMap = new HashMap<>();
    public static WorkspaceDecorator getInstance(Workspace workspace){
        if (!workspaceWorkspaceDecoratorHashMap.containsKey(workspace)) {
            workspaceWorkspaceDecoratorHashMap.put(workspace, new WorkspaceDecorator(workspace));
        }
        return workspaceWorkspaceDecoratorHashMap.get(workspace);
    }

    protected Workspace workspace;
    protected JsonObject comment;
    protected File commentFile;

    private WorkspaceDecorator(Workspace workspace) {
        this.workspace = workspace;
        commentFile = new File(workspace.getWorkspaceFolder(), "comment.json");
        if (commentFile.exists()) {
            try {
                this.comment = new Gson().fromJson(new FileReader(commentFile), JsonObject.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        if (comment == null){
            comment = new JsonObject();
        }
    }

    public Optional<String> getCommentOfElement(ModElement modElement) {
        var registryName = modElement.getRegistryName();
        if (comment.has(registryName)) {
            var langToComment = comment.get(registryName).getAsJsonObject();
            var lang = PreferencesManager.PREFERENCES.ui.language.get().toLanguageTag();
            var defaultLang = L10N.DEFAULT_LOCALE.toLanguageTag();
            if (langToComment.has(lang)) {
                return Optional.of(langToComment.get(lang).getAsString());
            } else if (langToComment.has(defaultLang)) {
                return Optional.of(langToComment.get(defaultLang).getAsString());
            }
        }
        return Optional.empty();
    }

    public void setComment(ModElement modElement,@Nonnull String commentContent) {
        var registryName = modElement.getRegistryName();
        if (!comment.has(registryName)) {
            comment.add(registryName, new JsonObject());
        }
        var langToComment = comment.get(registryName).getAsJsonObject();
        var lang = PreferencesManager.PREFERENCES.ui.language.get().toLanguageTag();
        langToComment.add(lang,new JsonPrimitive(commentContent));

        try (FileWriter fileWriter = new FileWriter(commentFile)){
            new Gson().toJson(comment,fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasWeight(){
        return getDependants().stream()
                .noneMatch(str -> str.startsWith("weight_"));
    }

    public void addWeight(int weight){
        getDependants().stream()
                .filter(str -> str.startsWith("weight_")).forEach(a-> workspace.getWorkspaceSettings().dependants.remove(a));
        getWorkspaceSettings().dependants.add(WorkspaceUtils.weightDependant(weight));
    }

    public boolean hasSupportedVersion(long versionLong){
        return getWorkspaceSettings().dependants.stream().anyMatch(a->a.equals(WorkspaceUtils.supportedVersionDependant(versionLong)));
    }

    public void addSupportedVersion(long versionLong){
       getWorkspaceSettings().dependants.add(WorkspaceUtils.supportedVersionDependant(versionLong));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public WorkspaceSettings getWorkspaceSettings(){
        return workspace.getWorkspaceSettings();
    }

    /**
     * This can be used to add weight and supportedversion
     */
    public Set<String> getDependants(){
        return new HashSet<>(workspace.getWorkspaceSettings().dependants);
    }

    public File getWorkspaceFolder(){
        return workspace.getWorkspaceFolder();
    }

    public File getWorkspaceLibraryFile(){
        return new File(getWorkspaceFolder(), ".mcreator/libs");
    }

    public File getWorkspacePluginsFile(){
        return new File(getWorkspaceFolder(), "run/plugins");
    }
}
