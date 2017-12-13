using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;

namespace WebCBXT
{
    /// <summary>
    /// ReceiveServices 的摘要说明
    /// </summary>
    public class ReceiveServices : IHttpHandler
    {

        public void ProcessRequest(HttpContext context)
        {
            String col = "";
            String data = "";
            foreach (String f in HttpContext.Current.Request.Form)
            {
                col = col + "," + f;
                data = data + "," + HttpContext.Current.Request.Form[f];
            }
            if (!String.IsNullOrEmpty(data))
            {
                data = data.Substring(1, data.Length - 1);
                col = col.Substring(1, col.Length - 1);
                WriteLog(data, col);
            }


            for (int i = 0; i < HttpContext.Current.Request.Files.AllKeys.Length; i++)
            {
                HttpPostedFile file = HttpContext.Current.Request.Files[i];
                if (file != null)
                {
                    String path = System.Web.HttpContext.Current.Server.MapPath("~/ImgData/" + file.FileName);
                    if (File.Exists(path))
                        File.Delete(path);
                    file.SaveAs(path);
                }
            }
            context.Response.Write("succeed");
        }

        public bool IsReusable
        {
            get
            {
                return false;
            }
        }

        public static void WriteLog(string strLog,string col)
        {
            try
            {
                Boolean firstwrite = false;
                string sFilePath = "D:\\soft\\hlyapp\\CBXTWeb\\Data";
                string sFileName = DateTime.Now.ToString("yyyyMMdd") + ".csv";
                sFileName = sFilePath + "\\" + sFileName; //文件的绝对路径
                if (!Directory.Exists(sFilePath))//验证路径是否存在
                {
                    Directory.CreateDirectory(sFilePath);
                }
                FileStream fs;
                StreamWriter sw;
                if (File.Exists(sFileName))
                {
                    fs = new FileStream(sFileName, FileMode.Append, FileAccess.Write);
                    firstwrite = false;
                }
                else
                {
                    fs = new FileStream(sFileName, FileMode.Create, FileAccess.Write);
                    firstwrite = true;
                }
                sw = new StreamWriter(fs, Encoding.GetEncoding("gb2312"));
                if (firstwrite)
                    sw.WriteLine(col);
                sw.WriteLine(strLog);
                sw.Close();
                fs.Close();
            }
            catch { }
            
        }
    }
}