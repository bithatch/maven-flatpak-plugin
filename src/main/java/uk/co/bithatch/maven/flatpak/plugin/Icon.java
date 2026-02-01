package uk.co.bithatch.maven.flatpak.plugin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "icon")
public class Icon {

	private String type;
	private Integer width;
	private Integer height;
	private Integer scale;
	private String path;

	@JsonProperty(value = "type")
    @JacksonXmlProperty(isAttribute = true)
	public final String getType() {
		return type;
	}

	@JacksonXmlText
	public final String getPath() {
		return path;
	}

	public final void setPath(String path) {
		this.path = path;
	}

	@JsonProperty(value = "scale")
    @JacksonXmlProperty(isAttribute = true)
	public final Integer getScale() {
		return scale;
	}

	public final void setScale(Integer scale) {
		this.scale = scale;
	}

	public final void setType(String type) {
		this.type = type;
	}

	@JsonProperty(value = "width")
    @JacksonXmlProperty(isAttribute = true)
	public final Integer getWidth() {
		return width;
	}

	public final void setWidth(Integer width) {
		this.width = width;
	}

	@JsonProperty(value = "height")
    @JacksonXmlProperty(isAttribute = true)
	public final Integer getHeight() {
		return height;
	}

	public final void setHeight(Integer height) {
		this.height = height;
	}
}
