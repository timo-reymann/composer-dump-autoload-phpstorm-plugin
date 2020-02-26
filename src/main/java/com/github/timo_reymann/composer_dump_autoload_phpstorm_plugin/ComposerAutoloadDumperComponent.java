package com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin;

import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.command.ComposerCommandScheduler;
import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.listener.PhpFileListener;
import com.github.timo_reymann.composer_dump_autoload_phpstorm_plugin.util.MessageBusUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.php.composer.ComposerDataService;

/**
 * Plugin entrypoint component
 */
public class ComposerAutoloadDumperComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(ComposerAutoloadDumperComponent.class);

    private final Project project;

    public ComposerAutoloadDumperComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        ComposerDataService composerDataService = ComposerDataService.getInstance(project);
        ComposerCommandScheduler scheduler = new ComposerCommandScheduler(project);

        if (!composerDataService.isConfigWellConfigured()) {
            logger.warn("Disable for project");
            MessageBusUtil.showMessage(NotificationType.WARNING, "Autodump for composer has been disabled", "It seems you are working on a non-composer project, or your config is invalid");
            return;
        }

        logger.info("Register file listener for current project");
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new PhpFileListener(scheduler));
        scheduler.schedule();
    }
}
