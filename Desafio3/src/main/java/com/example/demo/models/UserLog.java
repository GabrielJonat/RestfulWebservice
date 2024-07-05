package com.example.demo.models;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="userlog")
public class UserLog {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private LocalDateTime data;

	    @Column(nullable = false)
	    private String acao;
	    
	    @Column(nullable = false)
	    private String userName;

	    public UserLog() {}
	    
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public LocalDateTime getData() {
			return data;
		}

		public void setData(LocalDateTime data) {
			this.data = data;
		}

		public String getAcao() {
			return acao;
		}

		public void setAcao(String acao) {
			this.acao = acao;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
	    
}
