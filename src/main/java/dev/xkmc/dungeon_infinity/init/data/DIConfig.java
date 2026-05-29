package dev.xkmc.dungeon_infinity.init.data;

import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.util.ConfigInit;

public class DIConfig {

	public static class Client extends ConfigInit {

		Client(Builder builder) {
			markL2();
		}

	}

	public static class Server extends ConfigInit {

		Server(Builder builder) {
			markL2();
		}

	}

	public static final Client CLIENT = DungeonInfinity.REGISTRATE.registerClient(Client::new);
	public static final Server SERVER = DungeonInfinity.REGISTRATE.registerSynced(Server::new);

	public static void init() {
	}


}
