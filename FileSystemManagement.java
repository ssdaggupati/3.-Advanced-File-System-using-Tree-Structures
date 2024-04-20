import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Date;

class FileAttributes {
    String name;
    String type;
    int size;
    Date creationDate;

    FileAttributes(String name, String type, int size, Date creationDate) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.creationDate = creationDate;
    }
}

class AVLNode {
    FileAttributes fileAttributes;
    AVLNode left, right;
    int height;

    AVLNode(FileAttributes fileAttributes) {
        this.fileAttributes = fileAttributes;
        this.height = 1;
    }
}

public class FileSystemManagement {
    private static AVLNode root;
    private static Map<String, AVLNode> index = new HashMap<>();

    private static int height(AVLNode node) {
        if (node == null) return 0;
        return node.height;
    }

    private static int balanceFactor(AVLNode node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    private static AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private static AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private static AVLNode insert(AVLNode node, FileAttributes fileAttributes) {
        if (node == null) return new AVLNode(fileAttributes);

        int compare = fileAttributes.name.compareTo(node.fileAttributes.name);
        if (compare < 0) {
            node.left = insert(node.left, fileAttributes);
        } else if (compare > 0) {
            node.right = insert(node.right, fileAttributes);
        } else {
            System.out.println("File with the same name already exists.");
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = balanceFactor(node);

        if (balance > 1 && fileAttributes.name.compareTo(node.left.fileAttributes.name) < 0) {
            return rightRotate(node);
        }
        if (balance < -1 && fileAttributes.name.compareTo(node.right.fileAttributes.name) > 0) {
            return leftRotate(node);
        }
        if (balance > 1 && fileAttributes.name.compareTo(node.left.fileAttributes.name) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && fileAttributes.name.compareTo(node.right.fileAttributes.name) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public static void insertFile(FileAttributes fileAttributes) {
        if (validateFileAttributes(fileAttributes)) {
            root = insert(root, fileAttributes);
            index.put(fileAttributes.name, root);
            System.out.println("File inserted successfully.");
        }
    }

    public static void deleteFile(String fileName) {
        root = delete(root, fileName);
        index.remove(fileName);
    }

    private static AVLNode minValueNode(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    private static AVLNode delete(AVLNode root, String fileName) {
        if (root == null) return root;

        int compare = fileName.compareTo(root.fileAttributes.name);
        if (compare < 0) {
            root.left = delete(root.left, fileName);
        } else if (compare > 0) {
            root.right = delete(root.right, fileName);
        } else {
            if (root.left == null || root.right == null) {
                AVLNode temp = root.left != null ? root.left : root.right;
                if (temp == null) {
                    temp = root;
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                AVLNode temp = minValueNode(root.right);
                root.fileAttributes = temp.fileAttributes;
                root.right = delete(root.right, temp.fileAttributes.name);
            }
        }

        if (root == null) return null;

        root.height = Math.max(height(root.left), height(root.right)) + 1;

        int balance = balanceFactor(root);

        if (balance > 1 && balanceFactor(root.left) >= 0) {
            return rightRotate(root);
        }
        if (balance > 1 && balanceFactor(root.left) < 0) {
            root.left = leftRotate(root.left);
            return rightRotate(root);
        }
        if (balance < -1 && balanceFactor(root.right) <= 0) {
            return leftRotate(root);
        }
        if (balance < -1 && balanceFactor(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }
    @SuppressWarnings("unused")
    private static void preorderTraversal(AVLNode node) {
        if (node != null) {
            System.out.print(node.fileAttributes.name + " ");
            preorderTraversal(node.left);
            preorderTraversal(node.right);
        }
    }

    public static void displayFiles() {
        if (root == null) {
            System.out.println("No files in the file system.");
        } else {
            System.out.println("Files in the file system:");
            displayFiles(root);
        }
    }

    private static void displayFiles(AVLNode node) {
        if (node != null) {
            displayFiles(node.left);
            System.out.println("Name: " + node.fileAttributes.name);
            System.out.println("Type: " + node.fileAttributes.type);
            System.out.println("Size: " + node.fileAttributes.size);
            System.out.println("Creation Date: " + node.fileAttributes.creationDate);
            System.out.println();
            displayFiles(node.right);
        }
    }

    private static boolean validateFileAttributes(FileAttributes fileAttributes) {
        if (fileAttributes.name.isEmpty() || fileAttributes.type.isEmpty() || fileAttributes.size <= 0) {
            System.out.println("Invalid file attributes. Please provide valid name, type, and size.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        String fileName;
        do {
            System.out.println("\nFile System Management");
            System.out.println("1. Insert File");
            System.out.println("2. Delete File");
            System.out.println("3. Display Files");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter file name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter file type: ");
                    String type = scanner.nextLine();
                    System.out.print("Enter file size: ");
                    int size = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    Date creationDate = new Date(); // Current date/time
                    FileAttributes attributes = new FileAttributes(name, type, size, creationDate);
                    insertFile(attributes);
                    break;
                case 2:
                    System.out.print("Enter file name to delete: ");
                    fileName = scanner.nextLine();
                    deleteFile(fileName);
                    System.out.println("File deleted successfully.");
                    break;
                case 3:
                    displayFiles();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } while (choice != 4);
        scanner.close();
    }
    /*
     * Test Case:
     * Input:
     * File System Management
     * 1. Insert File
     * 2. Delete File
     * 3. Display Files
     * 4. Exit
     * Enter your choice: 1
     * Enter file name: daa
     * Enter file type: txt
     * Enter file size: 3
     * 
     * Output:
     * File inserted successfully.
     * 
     * Input:
     * File System Management
     * 1. Insert File
     * 2. Delete File
     * 3. Display Files
     * 4. Exit
     * Enter your choice: 3
     * 
     * Files in the file system:
     * Name: daa
     * Type: txt
     * Size: 3
     * 
     * Output:
     * Creation Date: Thu Apr 18 16:28:00 EDT 2024
     * 
     * Input:
     * File System Management
     * 1. Insert File
     * 2. Delete File
     * 3. Display Files
     * 4. Exit
     * Enter your choice: 2
     * Enter file name to delete: daa
     * 
     * Output:
     * File deleted successfully.
     * 
     * Input:
     * File System Management
     * 1. Insert File
     * 2. Delete File
     * 3. Display Files
     * 4. Exit
     * Enter your choice: 3
     * 
     * Output:
     * No files in the file system.
     * 
     * Input:
     * File System Management
     * 1. Insert File
     * 2. Delete File
     * 3. Display Files
     * 4. Exit
     * 
     * Output:
     * Enter your choice: 4
     * Exiting...
     */

}
