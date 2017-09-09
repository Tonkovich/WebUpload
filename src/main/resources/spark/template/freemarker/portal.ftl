<#include "base.ftl">
<#macro head>
<head>
    <title>Student</title>
    <!-- Custom CSS style -->
    <style>
        table {
            box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
        }

        table td {
            padding: 15px;
        }

        th {
            background-color: black;
            color: white;
        }

        table tr.announcement-row:hover td {
            cursor: pointer;
            background-color: #d3d3d3;
        }
    </style>

    <script type="application/javascript">
        function sendFile(){
            var fd = new FormData($('#upload')[0]);
            var userID = ${userID};
            fd.append("userID", userID);
            $.ajax({
                type: "post",
                url: "/api/upload",
                contentType: false,
                processData: false,
                data: fd,
                success: function(){
                }
            })
            alert("File uploaded");
            setTimeout(function(){location.reload()}, 2000);
        }
    </script>
    <script type="application/javascript">
        function deleteFile(fileID){
            $.ajax({
                type: "post",
                url: "/api/delete",
                data: {fileID: fileID}
            });
            location.reload();
        }
    </script>
</head>
</#macro>
<#macro content>
<!-- Start Menu -->
<div class="row">
    <div class="col-md-2 col-md-offset-1">
        <h2>Menu</h2>
        <table width="200px">
            <tbody>
            <tr>
                <td>
                    <div class="row">
                        <div class="col-md-12">
                            <ul class="nav nav-pills nav-stacked">
                                <!-- These menus will load separate pages -->
                                <li class="active"><a data-toggle="tab" href="#files">Files</a></li>
                            </ul>
                        </div>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    <!-- End Menu -->
    <!-- Start download section -->
    <div class="col-md-6 col-md-offset-1">
        <div class="tab-content">
            <div id="files" class="tab-pane fade in active">
                <h2>Files</h2>
                <table width="600px">
                    <tbody>
                    <tr>
                        <td>
                            <!-- Upload Files-->
                            <form id="upload" enctype="multipart/form-data" method="get" onsubmit="sendFile()">
                                <label>Upload File: </label>
                                <label class="btn btn-default btn-file">
                                    <input id="fileInput" type="file" name="file" required>
                                </label>
                                <input type="submit" value="Upload">
                            </form>
                            </br>

                            <!-- List downloads -->
                            <table class="table table-striped">
                                <thead>
                                <th>File List</th>
                                <th></th>
                                </thead>
                                <tbody>
                                    <#list userFiles as files>
                                    <tr>
                                        <td><a style="cursor:pointer" href="/api/download?fileID=${files.getFileID()}">${files.getFileName()}</a> </td>
                                        <td><button onclick="deleteFile(this.value)" value="${files.getFileID()}" class="btn btn-danger btn-sm"><span class="glyphicon glyphicon-remove-sign"></span></button></td>
                                    </tr>
                                    </#list>
                                </tbody>
                            </table>
                            </br>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <!-- End menu items content -->
</div>
</#macro>
<@display_page userData/>