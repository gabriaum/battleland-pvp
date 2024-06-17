package com.gabriaum.arcade.game.list.lava.listener;

import com.gabriaum.arcade.ArcadeMain;
import com.gabriaum.arcade.game.Game;
import com.gabriaum.arcade.game.type.GameType;
import com.gabriaum.arcade.user.User;
import com.solexgames.core.member.PvPMember;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class LavaListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();

        if (event.getClickedBlock() != null && (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.WALL_SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST))) {
            Sign sign = (Sign) event.getClickedBlock().getState();

            if (sign != null) {
                System.out.println(sign.getLine(2));

                switch (sign.getLine(2).toLowerCase()) {
                    case "§a§lfácil": {
                        PvPMember member = user.getMember();

                        member.addXP(2);

                        player.sendMessage("§6§lLAVA §fVocê passou o nível §2Fácil§f com sucesso!");
                        player.sendMessage("§9§lXP §fVocê recebeu §9§l2 XPs");

                        game.onJoin(player);
                        break;
                    }

                    case "§e§lmédio": {
                        PvPMember member = user.getMember();

                        member.addXP(4);

                        player.sendMessage("§6§lLAVA §fVocê passou o nível §6Médio§f com sucesso!");
                        player.sendMessage("§9§lXP §fVocê recebeu §9§l4 XPs");

                        game.onJoin(player);
                        break;
                    }

                    case "§c§ldifícil": {
                        PvPMember member = user.getMember();

                        member.addXP(6);

                        player.sendMessage("§6§lLAVA §fVocê passou o nível §cDifícil§f com sucesso!");
                        player.sendMessage("§9§lXP §fVocê recebeu §9§l6 XPs");

                        game.onJoin(player);
                        break;
                    }

                    case "§4§lextremo": {
                        PvPMember member = user.getMember();

                        member.addXP(8);

                        player.sendMessage("§6§lLAVA §fVocê passou o nível §4Extremo§f com sucesso!");
                        player.sendMessage("§9§lXP §fVocê recebeu §9§l8 XPs");

                        game.onJoin(player);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fireTick(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        User user = ArcadeMain.getPlugin().getUserManager().get(player.getUniqueId());

        if (user == null)
            return;

        Game game = user.getGame();

        if (game.getType().equals(GameType.LAVA) && event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))
            event.setCancelled(true);
    }
}
