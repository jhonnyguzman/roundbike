package com.roundbike.entities;

public class Bicicleterias {
	
	private int _id;
	private String nombre;
	private String descripcion;
	private String domicilio;
	private String telefono;
	private String email = "";
	private String sitioweb;
	private String lat;
	private String lon;
	private int estado;
	private String estadoDescripcion;
	private int servicio;
	private String servicioDescripcion;
	private String created_at;
	private String updated_at;
	private int distance;
	
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getDomicilio() {
		return domicilio;
	}
	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSitioweb() {
		return sitioweb;
	}
	public void setSitioweb(String sitioweb) {
		this.sitioweb = sitioweb;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public int getEstado() {
		return estado;
	}
	public void setEstado(int estado) {
		this.estado = estado;
	}
	public int getServicio() {
		return servicio;
	}
	public void setServicio(int servicio) {
		this.servicio = servicio;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
	public String getestadoDescripcion() {
		if(this.estado == 1){
			this.estadoDescripcion = "Disponible";
		}else if(this.estado == 2){
			this.estadoDescripcion = "No disponible";
		}
		return this.estadoDescripcion;
	}
	
	public String getServicioDescripcion() {
		if(this.servicio == 1){
			this.servicioDescripcion = "Gratis";
		}else if(this.servicio == 2){
			this.servicioDescripcion = "Pago";
		}
		return this.servicioDescripcion;
	}
	
	public void setDistance(int distance) {
		this.distance= distance;
	}
	public int getDistance() {
		return distance;
	}
}
