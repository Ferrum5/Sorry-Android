import java.io.*;

public class ConvertAss2Json {
    public static void main(String args[]) {
        File templateDir = new File("D:\\playground\\templates");
        File[] files = templateDir.listFiles((dir, name) -> name.endsWith(".ass"));
        if (files == null || files.length == 0) {
            System.out.println("当前路径无字幕文件");
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length() - 4);
            System.out.println(String.format("正在处理第%d个字幕文件%s", i + 1, fileName));
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"").append("name").append("\"").append(":");
            sb.append("\"").append(fileName).append("\"");
            sb.append(",");
            sb.append("\"").append("ass").append("\"").append(":").append("[");
            int jsonIndex = 0;

            try {
                BufferedReader rd = new BufferedReader(new FileReader(file));
                String line;
                while ((line = rd.readLine()) != null) {
                    if (line.startsWith("Dialogue:")) {
                        if (jsonIndex++ > 0) {
                            sb.append(",");
                        }
                        String[] values = line.substring(8).trim().split(",");
                        sb.append("{")
                                .append("\"").append("start").append("\"")
                                .append(":")
                                .append("\"").append(values[1].trim()).append("\"")
                                .append(",")
                                .append("\"").append("end").append("\"")
                                .append(":")
                                .append("\"").append(values[2].trim()).append("\"")
                                .append(",")
                                .append("\"").append("hint").append("\"")
                                .append(":")
                                .append("\"").append("第").append(jsonIndex).append("条字幕").append("\"")
                                .append("}");

                    }
                }
                rd.close();
                sb.append("]").append("}");
                File outputFile = new File(file.getParent(), fileName + ".json");
                FileWriter fw = new FileWriter(outputFile);
                fw.write(sb.toString());
                fw.flush();
                fw.close();
            } catch (Exception e) {
                System.out.println(String.format("第%d个文件%s转换失败", i + 1, file.getName()));
            }
            System.out.println(sb.toString());
        }
    }
}
