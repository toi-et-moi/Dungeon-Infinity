package dev.xkmc.dungeon_infinity.content.config;

import com.mojang.datafixers.util.Pair;
import dev.xkmc.dungeon_infinity.init.DungeonInfinity;
import dev.xkmc.l2core.serial.config.BaseConfig;
import dev.xkmc.l2core.serial.config.CollectType;
import dev.xkmc.l2core.serial.config.ConfigCollect;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;

import java.util.*;

@SerialClass
public class TemplateConfig extends BaseConfig {

	public static CompiledSet of(String path) {
		return DungeonInfinity.TEMPLATES.getMerged().cache.get(path);
	}

	@ConfigCollect(CollectType.MAP_OVERWRITE)
	@SerialField
	public final LinkedHashMap<String, LinkedHashMap<Identifier, TemplateData>> templates = new LinkedHashMap<>();

	private final LinkedHashMap<String, CompiledSet> cache = new LinkedHashMap<>();

	@Override
	protected void postMerge() {
		for (var ent : templates.entrySet()) {
			cache.put(ent.getKey(), new CompiledSet(ent.getValue()));
		}
	}

	public StyleBuilder start(String style) {
		return new StyleBuilder(this, style);
	}

	public record TemplateData(int weight) {

	}

	public static class CompiledSet {

		private final String[] ids;
		private final CompiledRoom[] data;

		private CompiledSet(Map<Identifier, TemplateData> map) {
			Map<String, List<Pair<String, TemplateData>>> split = new LinkedHashMap<>();
			for (var ent : map.entrySet()) {
				var id = ent.getKey();
				split.computeIfAbsent(id.getNamespace(), _ -> new ArrayList<>())
						.add(Pair.of(id.getPath(), ent.getValue()));
			}
			List<String> keys = new ArrayList<>(split.keySet());
			keys.sort(Comparator.comparing(e -> e));
			int n = keys.size();
			ids = new String[n];
			data = new CompiledRoom[n];
			for (int i = 0; i < n; i++) {
				ids[i] = keys.get(i);
				data[i] = new CompiledRoom(split.get(ids[i]));
			}
		}

		public int styleCount() {
			return ids.length;
		}

		public int variantCount(int i) {
			return data[i].ids.length;
		}

		public TemplateData variant(int i, int j) {
			return data[i].data[j];
		}

		public String path(String room, int i, int j) {
			return ids[i] + "/" + room + data[i].ids[j];
		}

		public int getRandom(int i, RandomSource rand) {
			return data[i].weighted.getRandomOrThrow(rand);
		}

	}

	private static class CompiledRoom {

		private final String[] ids;
		private final TemplateData[] data;
		private final WeightedList<Integer> weighted;

		private CompiledRoom(List<Pair<String, TemplateData>> list) {
			list.sort(Comparator.comparing(Pair::getFirst));
			int n = list.size();
			ids = new String[n];
			data = new TemplateData[n];
			var builder = WeightedList.<Integer>builder();
			for (int i = 0; i < n; i++) {
				ids[i] = list.get(i).getFirst();
				data[i] = list.get(i).getSecond();
				builder.add(i, data[i].weight());
			}
			weighted = builder.build();
		}

	}

	public static class StyleBuilder {

		private final TemplateConfig config;
		private final String style;

		private StyleBuilder(TemplateConfig config, String style) {
			this.config = config;
			this.style = style;
		}

		public VariantBuilder room(String room) {
			return new VariantBuilder(this, room);
		}

		public TemplateConfig end() {
			return config;
		}

	}

	public static class VariantBuilder {

		private final StyleBuilder parent;
		private final String room;

		private VariantBuilder(StyleBuilder parent, String room) {
			this.parent = parent;
			this.room = room;
		}

		public VariantBuilder variant(String suffix, int weight) {
			parent.config.templates.computeIfAbsent(room, k -> new LinkedHashMap<>())
					.put(Identifier.fromNamespaceAndPath(parent.style, suffix), new TemplateData(weight));
			return this;
		}

		public StyleBuilder end() {
			return parent;
		}

	}

}
