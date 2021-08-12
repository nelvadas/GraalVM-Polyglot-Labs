package com.oracle.graalvm.demos;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;

public final class CovidDtoTable {

    public DepartmentIdProxyArrayColumn departmentId;
    public DepartmentNameProxyArrayColumn departmentName;
    public CsvFilePathProxyArrayColumn csvFilePath;


    public CovidDtoTable (CovidDto[] dto) {
        this.departmentId= new DepartmentIdProxyArrayColumn(dto);
        this.departmentName= new DepartmentNameProxyArrayColumn(dto);
        this.csvFilePath= new CsvFilePathProxyArrayColumn(dto);

    }

    public static final class CovidDto {
        public String departmentId;
        public String csvFilePath;
        public String departmentName;
        public CovidDto( String departmentId,String csvFilePath, String departmentName) {
            this.departmentId=departmentId;
            this.csvFilePath=csvFilePath;
            this.departmentName=departmentName;
        }

    }

    public static class DepartmentIdProxyArrayColumn implements ProxyArray {
        private final CovidDto[] dto;
        public DepartmentIdProxyArrayColumn(CovidDto[] dto) {
            this.dto = dto;
        }
        public Object get(long index) {
            return dto[(int) index].departmentId;
        }
        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }
        public long getSize() {
            return dto.length;
        }
    }



    public static class DepartmentNameProxyArrayColumn implements ProxyArray {
        private final CovidDto[] dto;

        public DepartmentNameProxyArrayColumn(CovidDto[] dto) {
            this.dto = dto;
        }

        public Object get(long index) {
            return dto[(int) index].departmentName;
        }

        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }

        public long getSize() {
            return dto.length;
        }
    }

    public static class CsvFilePathProxyArrayColumn implements ProxyArray {
        private final CovidDto[] dto;

        public CsvFilePathProxyArrayColumn(CovidDto[] dto) {
            this.dto = dto;
        }

        public Object get(long index) {
            return dto[(int) index].csvFilePath;
        }

        public void set(long index, Value value) {
            throw new UnsupportedOperationException();
        }

        public long getSize() {
            return dto.length;
        }
    }


}