using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace WebCBXT
{
    /// <summary>
    /// ReceivedPointFile 的摘要说明
    /// </summary>
    public class ReceivedPointFile : IHttpHandler
    {
        public void ProcessRequest(HttpContext context)
        {
            context.Response.ContentType = "text/plain";
            try
            {
                string isCommit = HttpContext.Current.Request.Headers.Get("isCommit");//.Params["isCommit"];
                string fileName = HttpContext.Current.Request.Headers.Get("filename");//.Params["filename"];
                long npos = Convert.ToInt64(HttpContext.Current.Request.Headers.Get("offset"));//.Params["offset"]);
                long size = Convert.ToInt64(HttpContext.Current.Request.Headers.Get("filesize"));//.Params["offset"]);
                int upLoadLength = Convert.ToInt32(HttpContext.Current.Request.InputStream.Length);
                if (Math.Abs(size - upLoadLength) > 5)
                {
                    context.Response.Write("fail");
                    return;
                }
                string path = System.Web.HttpContext.Current.Server.MapPath("~/ImgData/" + fileName);
                if (isCommit == "true")
                {
                    try
                    {
                        File.Delete(path);
                    }
                    catch { }
                }
                else { }
                if (!String.IsNullOrEmpty(fileName))
                {
                    FileStream fStream = new FileStream(path, FileMode.OpenOrCreate, FileAccess.ReadWrite);
                    fStream.Seek(npos, SeekOrigin.Begin);
                    BinaryReader bReader = new BinaryReader(HttpContext.Current.Request.InputStream);
                    try
                    {
                        byte[] data = new byte[upLoadLength];
                        bReader.Read(data, 0, upLoadLength);
                        fStream.Write(data, 0, upLoadLength);
                    }
                    catch
                    {
                        //TODO 添加异常处理
                    }
                    finally
                    {
                        //释放流
                        fStream.Close();
                        bReader.Close();
                    }
                    context.Response.Write("succeed");
                    return;
                }
                context.Response.Write("fail");
            }
            catch(Exception err) {
                context.Response.Write(err.ToString());
            }
        }

        public bool IsReusable
        {
            get
            {
                return false;
            }
        }
    }
}