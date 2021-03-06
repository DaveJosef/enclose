package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public interface MyPluginIcons {
    Icon ListCallsAction = IconLoader.getIcon("/icons/ChainIcon.png", MyPluginIcons.class); // Créditos da Imagem: https://www.onlinewebfonts.com/icon/448372
    Icon ToggleBreakerOnes = IconLoader.getIcon("icons/PadlockIcon.png", MyPluginIcons.class); // Créditos da Imagem: https://www.flaticon.com/free-icon/unlocked-padlock_39734#
    Icon ChangeBreakerOnes = IconLoader.getIcon("icons/HammerIcon.png", MyPluginIcons.class); // "Créditos da Imagem: https://www.flaticon.com/free-icon/hammer_497457
    Icon ChangeBreakerOnesStar = IconLoader.getIcon("icons/HammerIconStar.png", MyPluginIcons.class); // "Créditos da Imagem: https://www.flaticon.com/free-icon/hammer_497457
}
