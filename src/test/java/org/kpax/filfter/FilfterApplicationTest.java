package org.kpax.filfter;

import org.kpax.filfter.entity.Child;
import org.kpax.filfter.entity.Parent;
import org.kpax.filfter.entity.Subchild;
import org.kpax.filfter.repository.ChildRepository;
import org.kpax.filfter.repository.ParentRepository;
import org.kpax.filfter.repository.SubchildRepository;
import org.kpax.filfter.repository.support.QuerydslExecutorJpaRepositoryImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = QuerydslExecutorJpaRepositoryImpl.class)
public class FilfterApplicationTest {

	public static void main(String[] args) {
		SpringApplication.run(FilfterApplicationTest.class, args);
	}

	@Bean
	CommandLineRunner init(ParentRepository parentRepository, ChildRepository childRepository, SubchildRepository subchildRepository) {
		return args -> {
			Parent parent = new Parent("pfield", 1, 100.5, new BigDecimal(1));
			Child child = new Child("cfield", 50, 1.234, new BigDecimal(1000), new Date());
			//parent.getChildren().add(child);
			Subchild subchild = new Subchild("scfield", 500, 1.2345, new BigDecimal(10000), new Date());
			//child.getSubchildren().add(subchild);

			child.setParent(parent);
			subchild.setChildParent(child);

			//parentRepository.save(parent);
			subchildRepository.save(subchild);
			parentRepository.findAll().forEach(System.out::println);
		};
	}

}
