package net.primegap.authexample;

public class Opname {
	//"product_id":1,"location_id":1,"book_count":100,"actual_count":99,"note":null,"url":"http://tranquil-sands-8533.herokuapp.com/opnames/67.json
	//","product":{"name":"Nike Magista Opus Firm Ground 
	//(Volt)"},"location":{"description":"Z1S1H1"})
		String id,location_id,book_count,actual_count,note,url,location,product_name;
		public Opname(String id,String location_id,String book_count,String actual_count,
				String note,String url,String location,String product_name){
			this.id=id;
			this.location_id=location_id;
			this.book_count=book_count;
			this.actual_count=actual_count;
			this.note=note;
			this.url=url;
			this.location=location;
			this.product_name=product_name;
		}
		public void setLocation_id(String location_id){
			this.location_id=location_id;
		}
		public String getLocation_id(){
			return this.location_id;
		}
		
		
		public void setBook_count(String book_count){
			this.book_count=book_count;
		}
		public String getBook_count(){
			return this.book_count;
		}
		
		public void setActual_count(String actual_count){
			this.actual_count=actual_count;
		}
		public String getActual_count(){
			return this.actual_count;
		}
		
		public void setNote(String note){
			this.note=note;
		}
		public String getNote(){
			return this.note;
		}
		
		public void setUrl(String url){
			this.url=url;
		}
		public String getUrl(){
			return this.url;
		}
		
		public void setLocation(String location){
			this.location=location;
		}
		public String getLocation(){
			return this.location;
		}
		
		public void setProduct_name(String product_name){
			this.product_name=product_name;
		}
		public String getProduct_name(){
			return this.product_name;
		}
		
		
		
		public void setId(String id){
			this.id=id;
		}
		public String getId(){
			return this.id;
		}
}
