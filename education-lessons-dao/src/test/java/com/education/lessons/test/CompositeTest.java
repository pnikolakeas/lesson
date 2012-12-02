package com.education.lessons.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.education.lessons.dao.model.composite.Composite;
import com.education.lessons.dao.model.composite.CompositeTypeEnum;
import com.education.lessons.dao.service.composite.CompositeDao;
import com.education.lessons.dao.service.composite.CompositeService;

public class CompositeTest extends BaseTest {

	private Composite root;

	private CompositeDao compositeDao;
	private CompositeService compositeService;

	public CompositeTest(String name) {
		super(name);

		compositeDao = getBean("compositeDao");
		compositeService = getBean("compositeService");
	}

	@Override
	protected void setUp() {
		super.setUp();

		root = new Composite();
		root.setTitle("Root");
		root.setType(CompositeTypeEnum.ROOT);

		TransactionTemplate template = createTransactionTemplate();
		template.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				compositeDao.insert(root);
				return null;
			}
		});
	}

	@Override
	protected void tearDown() {
		TransactionTemplate template = createTransactionTemplate();
		template.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				compositeDao.delete(Composite.class, root.getId());
				return null;
			}
		});

		super.tearDown();
	}

	public void testCreateLesson() {
		Composite lesson = compositeService.createLesson("KOKO", "TOTO");

		assertNotNull("lesson should not be null", lesson);
		assertNotNull("lesson.id error", lesson.getId());
		assertEquals("lesson.title error", "KOKO", lesson.getTitle());
		assertEquals("lesson.title error", "TOTO", lesson.getDescription());

		assertNotNull("lesson.children should not be null", lesson.getChildren());
		assertEquals("lesson.children error", 4, lesson.getChildren().size());
		
		print(lesson, 0);
	}

	public void testGetRoot() {
		Composite lesson = compositeService.createLesson("KOKO", "MOMO");
		log(lesson.getClass().getName());
		
		Composite lesson1 = compositeService.createLesson("MOMO", "LOLO");
		log(lesson1.getClass().getName());

		compositeService.createPresentation(lesson.getChildren().get(2).getId());
		compositeService.createLink(lesson.getChildren().get(1).getId());
		compositeService.createLink(lesson.getChildren().get(1).getId());
		compositeService.createTutorial(lesson.getChildren().get(0).getId());
		compositeService.createAssignment(lesson.getChildren().get(3).getId());

		Composite root = compositeService.getRoot();
		print(root, 0);

		assertEquals("root.children error", 2, root.getChildren().size());
		assertEquals("lesson.children error", 4, root.getChildren().get(0).getChildren().size());
		assertEquals("lesson.children error", 4, root.getChildren().get(1).getChildren().size());
		assertEquals("lesson.children error", 1, root.getChildren().get(0).getChildren().get(0).getChildren().size());

		log(root.getClass().getName());
	}

	public void testDelete() {
		Composite lesson = compositeService.createLesson("KOKO", "ZOZO");
		log(lesson.getClass().getName());

		Composite root = compositeService.getRoot();
		print(root, 0);

		assertEquals("root.children error", 1, root.getChildren().size());

		List<Composite> children = root.getChildren().get(0).getChildren();
		assertEquals("lesson.children error", 4, children.size());

		Composite firstChild = children.get(0);
		compositeService.deleteComposite(firstChild.getId());

		root = compositeService.getRoot();
		print(root, 0);

		List<Composite> newChildren = root.getChildren().get(0).getChildren();
		assertEquals("lesson.children error", 3, newChildren.size());

		compositeService.deleteComposite(lesson.getId());

		root = compositeService.getRoot();
		print(root, 0);

		assertEquals("root.children error", 0, root.getChildren().size());

		log(root.getClass().getName());
	}
	
	public void testUpdate() {
		Composite lesson = compositeService.createLesson("KOKO", "ZOZO");
		
		assertNotNull("lesson should not be null", lesson);
		assertNotNull("lesson.id error", lesson.getId());
		
		print(lesson, 0);

		Integer lessonId = lesson.getId();
		
		Composite newLesson = compositeService.getCompositeWithChildren(lessonId);
		
		assertNotNull("newLesson should not be null", newLesson);
		assertNotNull("newLesson.id error", newLesson.getId());
		
		print(newLesson, 0);
		
		newLesson.setDescription("LALALA");
		newLesson.setFilePath("/fdf/fdfd/");
		
		compositeService.updateComposite(newLesson);
		
		Composite newLesson1 = compositeService.getCompositeWithChildren(lessonId);
		
		assertEquals("newLesson1.description error", "LALALA", newLesson1.getDescription());
		assertEquals("newLesson1.filepath error", "/fdf/fdfd/", newLesson1.getFilePath());
		
		print(newLesson, 0);
	}
	
	public void testRemove() {
		Composite lesson = compositeService.createLesson("KOKO", "MOMO");
		log(lesson.getClass().getName());
		
		Composite lesson1 = compositeService.createLesson("MOMO", "LOLO");
		log(lesson1.getClass().getName());

		compositeService.createPresentation(lesson.getChildren().get(2).getId());
		compositeService.createLink(lesson.getChildren().get(1).getId());
		compositeService.createLink(lesson.getChildren().get(1).getId());
		compositeService.createTutorial(lesson.getChildren().get(0).getId());
		compositeService.createAssignment(lesson.getChildren().get(3).getId());

		Composite root = compositeService.getRoot();
		print(root, 0);

		assertEquals("root.children error", 2, root.getChildren().size());
		assertEquals("lesson.children error", 4, root.getChildren().get(0).getChildren().size());
		assertEquals("lesson.children error", 4, root.getChildren().get(1).getChildren().size());
		assertEquals("lesson.children error", 1, root.getChildren().get(0).getChildren().get(0).getChildren().size());
		
//		Composite newRoot = compositeService.removeTreeContent(lesson.getId());
//		print(newRoot, 0);
//		assertEquals("newRoot.children error", 1, newRoot.getChildren().size());
//		
//		Composite newRoot1 = compositeService.removeTreeContent(lesson1.getId());
//		print(newRoot1, 0);
//		assertEquals("newRoot1.children error", 0, newRoot1.getChildren().size());
		
		compositeService.removeAllChildrenTreeContent(root.getChildren().get(0).getId());
		
		Composite newRoot1 = compositeService.getRoot();
		
		print(newRoot1, 0);
		assertEquals("newRoot1.children error", 2, newRoot1.getChildren().size());

		log(root.getClass().getName());
	}
	
	public void addTreeContent() {
		Composite lesson = compositeService.createLesson("KOKO", "MOMO");
		log(lesson.getClass().getName());
		
		Composite lesson1 = compositeService.createLesson("MOMO", "LOLO");
		log(lesson1.getClass().getName());

		Composite root1 = compositeService.addTreeContent(lesson.getChildren().get(3).getId());
		print(root1, 0);
		
		Composite root2 = compositeService.addTreeContent(lesson.getChildren().get(2).getId());
		print(root2, 0);
		
		Composite root3 = compositeService.addTreeContent(lesson.getChildren().get(1).getId());
		print(root3, 0);
		
		Composite root4 = compositeService.addTreeContent(lesson.getChildren().get(0).getId());
		print(root4, 0);

		Composite root5 = compositeService.getRoot();
		print(root5, 0);
	}
	
	public void displayContent() {
		Composite lesson = compositeService.createLesson("KOKO", "MOMO");
		log(lesson.getClass().getName());
		
		Composite root1 = compositeService.addTreeContent(lesson.getChildren().get(3).getId());
		print(root1, 0);
		
		Integer assID = root1.getChildren().get(0).getChildren().get(3).getChildren().get(0).getId();
		System.out.println("AAAAAAAAAAAAAAA: " +assID);
		assertNotNull("ass.id error", assID);
		
		Composite root = compositeService.assosiateUploadedFile(assID, "MOMOMOMO", "/fdfd/fdfdf/dfd");
		
		assertEquals("root.children error", "MOMOMOMO", root.getChildren().get(0).getChildren().get(3).getChildren().get(0).getKeyword());
		assertEquals("root.children error", "/fdfd/fdfdf/dfd", root.getChildren().get(0).getChildren().get(3).getChildren().get(0).getFilePath());
	
		String kokomploko = compositeService.displayNode(assID);
		System.out.print("ASDFGHJKLFKKHGFSDWHBFKJSDFHSKDHSJDBJSBD<BSD<S" + kokomploko);
	}
	
	public void testMove() {
		Composite lesson = compositeService.createLesson("KOKO", "MOMO");
		log(lesson.getClass().getName());
		
		Composite lesson1 = compositeService.createLesson("MOMO", "LOLO");
		log(lesson1.getClass().getName());

		compositeService.addTreeContent(lesson.getChildren().get(3).getId());
		compositeService.addTreeContent(lesson.getChildren().get(2).getId());
		compositeService.addTreeContent(lesson.getChildren().get(1).getId());
		Composite root4 = compositeService.addTreeContent(lesson.getChildren().get(0).getId());
		print(root4, 0);
		
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(14);
		
		List<Integer> ids1 = new ArrayList<Integer>();
		ids1.add(15);
		
		compositeService.moveTreeContent(9, ids);
		Composite root5 = compositeService.moveTreeContent(8, ids1);
		print(root5, 0);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		//suite.addTest(new CompositeTest("testCreateLesson"));
	    //suite.addTest(new CompositeTest("testGetRoot"));
		//suite.addTest(new CompositeTest("testDelete"));
		//suite.addTest(new CompositeTest("testUpdate"));
		//suite.addTest(new CompositeTest("testRemove"));
		//suite.addTest(new CompositeTest("addTreeContent"));
		//suite.addTest(new CompositeTest("displayContent"));
		suite.addTest(new CompositeTest("testMove"));
		return suite;
	}

	// JUST FOR DEBUGGING PURPOSES
	public static void print(Composite c, int indent) {

		for (int i = 0; i < indent; i++)
			System.out.print(" ");

		if(c != null) {
			System.out.println(" TYPE: " + c.getType() + " TITLE: " + c.getTitle() + " ID: " + c.getId());

			if (!c.isTerminal()) {
				for (Iterator<Composite> i = c.getChildren().iterator(); i.hasNext();) {
					Composite cn = i.next();
					print(cn, indent + 2);
				}
			}
		}
	}
}
