package de.crysxd.ownfx;

import java.awt.Color;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

public class ColorStop {

	private Color color;
	private int ledIndex;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getLedIndex() {
		return ledIndex;
	}

	public void setLedIndex(int ledIndex) {
		this.ledIndex = ledIndex;
	}


	public static class JsonDeserializer implements com.google.gson.JsonDeserializer<ColorStop> {

		@Override
		public ColorStop deserialize(JsonElement element, Type type,
				JsonDeserializationContext context) throws JsonParseException {

			ColorStop c = new ColorStop();

			JsonObject json = element.getAsJsonObject();

			JsonElement colorElement = json.get("color");
			JsonElement ledIndexElement = json.get("ledIndex");

			c.setLedIndex(ledIndexElement.getAsInt());
			c.setColor(Color.decode(colorElement.getAsString()));

			return c;
			
		}
	}


	public static class JsonSerializer implements com.google.gson.JsonSerializer<ColorStop> {

		@Override
		public JsonElement serialize(ColorStop cs, Type type,
				JsonSerializationContext context) {
			
			JsonObject json = new JsonObject();
			json.addProperty("color", "#" + Integer.toHexString(cs.getColor().getRGB()).substring(2));
			json.addProperty("ledIndex", cs.getLedIndex());

			return json;
			
		}
	}
}
