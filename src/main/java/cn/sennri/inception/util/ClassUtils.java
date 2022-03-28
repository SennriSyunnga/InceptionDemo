package cn.sennri.inception.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 原作者 孤傲苍狼
 * 出处
 * https://www.cnblogs.com/xdp-gacl/p/4010328.html
 * 修改 Sennri
 */
@Slf4j
public class ClassUtils {
    private ClassUtils() {
    }

    /**
     * 获取类所在包下的所有类
     * @param clazz
     * @return
     */
    public static Collection<Class<?>> getAllClassesInTheSamePackage(Class<?> clazz) throws IOException, ClassNotFoundException {
        String packName = org.apache.commons.lang3.ClassUtils.getPackageName(clazz);
        return getClasses(packName);
    }

    /**
     * 从包package中获取所有的Class
     * 方法是在目录中递归的
     * 方法会识别内部类
     * @param pack
     * @return
     */
    public static Collection<Class<?>> getClasses(String pack) throws IOException, ClassNotFoundException {
        return getClasses(pack, true, true);
    }

    /**
     * 这里会保存调用者所在的ClassLoader
     */
    private final static ClassLoader THIS_CLASS_LOADER = Thread.currentThread().getContextClassLoader();

    /**
     * 获取某个包（可以包括其子包）路径下所有Class<?>对象
     * @param packageName   包名
     * @param recursive 是否在目录中采取递归的文件搜索策略
     * @param includeInnerClass 是否接纳内部类
     * @return 一个包路径下的所有Class的集合
     */
    public static Collection<Class<?>> getClasses(String packageName, boolean recursive, boolean includeInnerClass)
            throws IOException, ClassNotFoundException {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs = THIS_CLASS_LOADER.getResources(packageDirName);
        // 循环迭代下去
        while (dirs.hasMoreElements()) {
            // 获取下一个元素
            URL url = dirs.nextElement();
            // 得到协议的名称
            String protocol = url.getProtocol();
            // 如果是以文件的形式保存在服务器上
            if ("file".equals(protocol)) {
                log.debug("file类型的扫描");
                // 获取包的物理路径
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                // 以文件的方式扫描整个包下的文件 并添加到集合中
                findAndAddClassesInPackageByFile(packageName, filePath, recursive, includeInnerClass, classes);
            }
            // 如果是jar包文件
            else if ("jar".equals(protocol)) {
                log.debug("jar类型的扫描");
                // 获取jar
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                // 从此jar包 得到一个枚举类
                Enumeration<JarEntry> entries = jar.entries();
                // 同样的进行循环迭代
                while (entries.hasMoreElements()) {
                    // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // 如果是以/开头的
                    if (name.charAt(0) == '/') {
                        // 获取后面的字符串
                        name = name.substring(1);
                    }
                    // 前半部分和定义的包名需要相同， 否则跳过
                    if (!name.startsWith(packageDirName)) {
                        continue;
                    }
                    int idx = name.lastIndexOf('/');
                    // 如果以"/"结尾 是一个包
                    if (idx != -1) {
                        // 获取包名 把"/"替换成"."
                        packageName = name.substring(0, idx).replace('/', '.');
                    }
                    // 如果可以迭代下去 并且是一个包 则往下走，否则立刻continue循环
                    if ((idx == -1) && !recursive) {
                        continue;
                    }
                    // 如果是一个.class文件 而且不是目录
                    if (name.endsWith(".class") && !entry.isDirectory()) {
                        // 去掉后面的".class" 获取真正的类名
                        String className = name.substring(packageName.length() + 1, name.length() - 6);

                        // 添加到classes
                        Class<?> clazz = THIS_CLASS_LOADER.loadClass(packageName + '.' + className);
                        // 若是顶层类，或者接受外部类时
                        if (includeInnerClass || !org.apache.commons.lang3.ClassUtils.isInnerClass(clazz)) {
                            classes.add(clazz);
                        }
                    }
                }
            }
        }
        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath,
                                                        final boolean recursive,
                                                        final boolean includeInnerClass,
                                                        Set<Class<?>> classes) throws ClassNotFoundException {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("用户定义包名{}下没有任何文件", packageName);
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirFiles = dir.listFiles(file -> (recursive && file.isDirectory()) || (file.getName().endsWith(".class")));
        // listFiles的Api设计感觉应该在没有结果时返回new File[0]才好，而且里面用的 toArray(new File[list.size()])也是不好的实践
        if (dirFiles != null) {
            // 循环所有文件
            for (File file : dirFiles) {
                // 如果是目录 则继续扫描
                if (file.isDirectory()) {
                    findAndAddClassesInPackageByFile(
                            packageName + "." + file.getName(),
                            file.getAbsolutePath(),
                            recursive,
                            includeInnerClass,
                            classes);
                } else {
                    // 如果是java类文件 去掉后面的.class 只留下类名
                    String className = file.getName().substring(0, file.getName().length() - 6);

                    // 添加到集合中去
                    Class<?> clazz = THIS_CLASS_LOADER.loadClass(packageName + '.' + className);
                    if (includeInnerClass || !org.apache.commons.lang3.ClassUtils.isInnerClass(clazz)) {
                        classes.add(clazz);
                    }
                }
            }
        }
    }
}
