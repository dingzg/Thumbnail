import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class Thumbnail {
	/**
	 * 生成缩略图 fromFileStr:原图片路径 saveToFileStr:缩略图路径 width:缩略图的宽 height:缩略图的高
	 */
	public static void saveImageAsJpg(String fromFileStr, String saveToFileStr,
			int width, int height, boolean equalProportion) throws Exception {
		BufferedImage srcImage;
		String imgType = "JPEG";
		if (fromFileStr.toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		File fromFile = new File(fromFileStr);
		File saveFile = new File(saveToFileStr);
		srcImage = ImageIO.read(fromFile);
		if (width > 0 || height > 0) {
			srcImage = resize(srcImage, width, height, equalProportion);
		}
		ImageIO.write(srcImage, imgType, saveFile);
	}

	/**
	 * 将原图片的BufferedImage对象生成缩略图 source：原图片的BufferedImage对象 targetW:缩略图的宽
	 * targetH:缩略图的高
	 */
	public static BufferedImage resize(BufferedImage source, int targetW,
			int targetH, boolean equalProportion) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		// 这里想实现在targetW，targetH范围内实现等比例的缩放
		// 如果不需要等比例的缩放则下面的if else语句注释调即可
		if (equalProportion) {
			if (sx > sy) {
				sx = sy;
				targetW = (int) (sx * source.getWidth());
			} else {
				sy = sx;
				targetH = (int) (sx * source.getHeight());
			}
		}
		if (type == BufferedImage.TYPE_CUSTOM) {
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW,
					targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else {
			target = new BufferedImage(targetW, targetH, type);
			Graphics2D g = target.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g.drawRenderedImage(source,
					AffineTransform.getScaleInstance(sx, sy));
			g.dispose();
		}
		return target;
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	public static void readFolder(File f) throws Exception {
		if (f != null) {
			if (f.isDirectory()) {
				File[] fileArray = f.listFiles();
				if (fileArray != null) {
					for (int i = 0; i < fileArray.length; i++) {
						// 递归调用
						readFolder(fileArray[i]);
					}
				}
			} else {
				System.out.println("File:" + f.getAbsolutePath());
				
				// skip .listing & Thumbs.db, only JPG
				String ext = getFileExtension(f);
				if ("JPG".equals(ext) || "GIF".equals(ext)) {
					
					// String fileNM = f.getName();

					System.out.println("Begin process... FileNM="
							+ f.getAbsolutePath());
					Thumbnail.saveImageAsJpg(f.getAbsolutePath(),
							f.getAbsolutePath() + ".new", 1024, 1024, true);

					System.out.println("Rename FileNM=" + f.getAbsolutePath());
					String filePath = f.getAbsolutePath() + ".new";
					f.delete();

					File newFile = new File(filePath);
					newFile.renameTo(f);

					System.out.println("Rename completed. FileNM="
							+ f.getAbsolutePath());
				}else{
					System.out.println("### Skip File:" + f.getAbsolutePath());
				}
			}
		}
	}

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				System.out.println("parm error");
			} else {
				System.out.println("Folder:" + args[0]);

				readFolder(new File(args[0]));
			}
			// read folder

			// C:\Projects\Sites\DO

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
